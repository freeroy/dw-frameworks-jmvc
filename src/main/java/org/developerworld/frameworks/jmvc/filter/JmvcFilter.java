package org.developerworld.frameworks.jmvc.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.developerworld.frameworks.jmvc.WebManager;

/**
 * jspMvc过滤器
 * 
 * @author Roy Huang
 * @version 20121218
 * 
 */
public class JmvcFilter extends AbstractJmvcFilter {

	@Override
	protected WebManager buildWebManager(HttpServletRequest request,
			HttpServletResponse response) {
		return new WebManager(request,response);
	}

}
