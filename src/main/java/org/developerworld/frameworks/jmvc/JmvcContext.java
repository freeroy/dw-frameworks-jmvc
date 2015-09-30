package org.developerworld.frameworks.jmvc;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.developerworld.commons.lang.util.OrderedProperties;

/**
 * jmvc框架上下文
 * 
 * @author Roy Huang
 * 
 */
public class JmvcContext {

	public final static String APPLICATION_ATTRIBUTE_JMVC_CONTEXT = "APPLICATION_ATTRIBUTE_JMVC_CONTEXT";

	private final static Log log = LogFactory.getLog(JmvcContext.class);

	private final static String PATH_SPLIT = ",";
	private final static String RG_B_STR = "${";
	private final static String RG_E_STR = "}";

	private String mappingConfig = "/WEB-INF/classes/config/jmvc_mapping.properties";
	private ConcurrentMap<String, String> vcMapping = new ConcurrentHashMap<String, String>();
	private ServletContext servletContext;

	private long cacheTime = 0;
	private Date cacheDate = new Date();
	private ConcurrentMap<String, Set<String>> cacheIncludePaths = new ConcurrentHashMap<String, Set<String>>();

	public JmvcContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		// 把当前对象寄存至application
		servletContext.setAttribute(APPLICATION_ATTRIBUTE_JMVC_CONTEXT, this);
	}

	public void setMappingConfig(String mappingConfig) {
		this.mappingConfig = mappingConfig;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	/**
	 * 重新加载
	 */
	private void refresh() {
		//若当前时间距离上次缓存时间超过指定的最长时间，则进行缓存刷新
		if (System.currentTimeMillis() - cacheDate.getTime() >= cacheTime) {
			synchronized (cacheDate) {
				if (System.currentTimeMillis() - cacheDate.getTime() >= cacheTime)
					reload();
			}
		}
	}

	/**
	 * 重新加载配置
	 */
	public void reload() {
		synchronized (cacheDate) {
			//重新设置缓存时间
			cacheDate.setTime(System.currentTimeMillis());
			//清空缓存记录集
			cacheIncludePaths.clear();
			//更新配置文件信息
			vcMapping.clear();
			vcMapping.putAll(getVCMapping(mappingConfig));
		}
	}

	/**
	 * 获取视图与控制也的绑定
	 * 
	 * @param configPath
	 * @return
	 */
	private Map<String, String> getVCMapping(String configPath) {
		Map<String, String> rst = new LinkedHashMap<String, String>();
		if (StringUtils.isNotBlank(configPath)) {
			try {
				//加载配置文件（按顺序加载）
				OrderedProperties properties = new OrderedProperties();
				properties.load(servletContext.getResourceAsStream(configPath));
				Set<String> keys = properties.stringPropertyNames();
				//把所有信息写入至返回的map
				for (String key : keys)
					rst.put(key, properties.getProperty(key));
			} catch (IOException e) {
				log.error(e);
			}
		}
		return rst;
	}

	/**
	 * 处理一个请求
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//执行对应控制层jsp
		doInclude(request, response, request.getServletPath());
		//执行目标访问地址
		filterChain.doFilter(request, response);
	}

	/**
	 * 执行包含页面
	 * 
	 * @param request
	 * @param response
	 * @param servletPath
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doInclude(HttpServletRequest request,
			HttpServletResponse response, String servletPath)
			throws ServletException, IOException {
		// 获取应该包含的路径
		Set<String> includePaths = getRequestIncludePaths(servletPath);
		// 若得出的路径不为空，则执行加载模板操作
		for (String includePath : includePaths) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(includePath);
			dispatcher.include(request, response);
		}
	}

	/**
	 * 获取include path 集合
	 * 
	 * @param servletPath
	 * @return
	 */
	public Set<String> getRequestIncludePaths(String servletPath) {
		refresh();
		// 若缓存无数据，则执行查找
		if (!cacheIncludePaths.containsKey(servletPath)) {
			Set<String> includePaths = new LinkedHashSet<String>();
			Set<Entry<String, String>> entrys = vcMapping.entrySet();
			for (Entry<String, String> entry : entrys) {
				String vPath = entry.getKey();
				String cPathStr = entry.getValue();
				if (StringUtils.isBlank(vPath) || StringUtils.isBlank(cPathStr))
					continue;
				String[] cPaths = cPathStr.split(PATH_SPLIT);
				Pattern rPattern = Pattern.compile(vPath);
				Matcher matcher = rPattern.matcher(servletPath);
				if (matcher.find()) {
					for (String cPath : cPaths) {
						cPath = getIncludePath(matcher, cPath);
						// 若路径对应文件不存在，则返回空
						if (isExistsPathFile(cPath)
								&& !cPath.equals(servletPath))
							includePaths.add(cPath);
					}
				}
			}
			// 写入缓存
			cacheIncludePaths.putIfAbsent(servletPath, includePaths);
		}
		return cacheIncludePaths.get(servletPath);
	}

	/**
	 * 获取匹配的cPath
	 * 
	 * @param matcher
	 * @param cPath
	 * @return
	 */
	private String getIncludePath(Matcher matcher, String cPath) {
		String rst = cPath;
		int bIndex = 0;
		int eIndex = 0;
		while (true) {
			bIndex = rst.indexOf(RG_B_STR, eIndex);
			if (bIndex < 0)
				break;
			eIndex = rst.indexOf(RG_E_STR, bIndex + RG_B_STR.length());
			if (eIndex < 0)
				break;
			String tmp = rst.substring(bIndex + RG_B_STR.length(), eIndex);
			int gI = Integer.parseInt(tmp.trim());
			String groupStr = null;
			if (gI >= 0 && gI <= matcher.groupCount())
				groupStr = matcher.group(gI);
			if (groupStr == null)
				groupStr = "";
			rst = rst.substring(0, bIndex) + groupStr
					+ rst.substring(eIndex + RG_E_STR.length(), rst.length());
			// 调整结束位置到修改内容后的位置
			eIndex = bIndex + groupStr.length();
		}
		return rst;
	}

	/**
	 * 判断是否存在
	 * 
	 * @param jspPath
	 * @return
	 */
	private boolean isExistsPathFile(String jspPath) {
		String filePath = servletContext.getRealPath(jspPath);
		File file = new File(filePath);
		return file.exists() && file.isFile();
	}
}
