package org.developerworld.frameworks.jmvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * web上下文
 * 
 * @author Roy Huang
 * @version 20121218
 * 
 */
public class WebManager {

	private static Log log = LogFactory.getLog(WebManager.class);

	private static ObjectMapper objectMapper = new ObjectMapper();

	private HttpServletRequest request;
	private HttpServletResponse response;

	public WebManager(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getRespone() {
		return response;
	}

	public ServletContext getServletContext() {
		return getSession().getServletContext();
	}

	public HttpSession getSession() {
		return request.getSession();
	}

	public HttpSession getSession(boolean rst) {
		return request.getSession(rst);
	}

	/**
	 * 返回日志工具
	 * 
	 * @return
	 */
	public Log getLog() {
		return log;
	}

	/**
	 * 返回json工具
	 * 
	 * @return
	 */
	public ObjectMapper getJsonObjectMapper() {
		return objectMapper;
	}
}
