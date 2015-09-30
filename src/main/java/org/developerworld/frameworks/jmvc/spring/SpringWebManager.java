package org.developerworld.frameworks.jmvc.spring;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.developerworld.frameworks.jmvc.WebManager;
import org.springframework.beans.BeansException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 针对spring的上下文对象
 * 
 * @author Roy Huang
 * @version 20121218
 * 
 */
public class SpringWebManager extends WebManager {

	private WebApplicationContext applicationContext;

	public SpringWebManager(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
		applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(request.getSession()
						.getServletContext());
	}

	public SpringWebManager(WebManager webContext) {
		this(webContext.getRequest(), webContext.getRespone());
	}

	/**
	 * 获取spring web上下文对象
	 * 
	 * @return
	 */
	public WebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @return
	 */
	public boolean containsBean(String arg0) {
		return applicationContext.containsBean(arg0);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @return
	 * @throws BeansException
	 */
	public Object getBean(String arg0) throws BeansException {
		return applicationContext.getBean(arg0);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @return
	 * @throws BeansException
	 */
	public <T> T getBean(Class<T> arg0) throws BeansException {
		return applicationContext.getBean(arg0);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws BeansException
	 */
	public <T> T getBean(String arg0, Class<T> arg1) throws BeansException {
		return applicationContext.getBean(arg0, arg1);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws BeansException
	 */
	public Object getBean(String arg0, Object... arg1) throws BeansException {
		return applicationContext.getBean(arg0, arg1);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws NoSuchMessageException
	 */
	public String getMessage(MessageSourceResolvable arg0, Locale arg1)
			throws NoSuchMessageException {
		return applicationContext.getMessage(arg0, arg1);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws NoSuchMessageException
	 */
	public String getMessage(String arg0, Object[] arg1, Locale arg2)
			throws NoSuchMessageException {
		return applicationContext.getMessage(arg0, arg1, arg2);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return
	 */
	public String getMessage(String arg0, Object[] arg1, String arg2,
			Locale arg3) {
		return applicationContext.getMessage(arg0, arg1, arg2, arg3);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @return
	 * @throws IOException
	 */
	public Resource[] getResources(String arg0) throws IOException {
		return applicationContext.getResources(arg0);
	}

	/**
	 * @see WebApplicationContext
	 * @param arg0
	 * @return
	 */
	public Resource getResource(String arg0) {
		return applicationContext.getResource(arg0);
	}

}
