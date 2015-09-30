package org.developerworld.frameworks.jmvc.spring.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.developerworld.frameworks.jmvc.WebManager;
import org.developerworld.frameworks.jmvc.filter.AbstractJmvcFilter;
import org.developerworld.frameworks.jmvc.spring.SpringWebManager;

/**
 * jspMvc过滤器
 * 
 * @author Roy Huang
 * @version 20121218
 * 
 */
public class SpringJmvcFilter extends AbstractJmvcFilter {

	@Override
	protected WebManager buildWebManager(HttpServletRequest request,
			HttpServletResponse response) {
		return new SpringWebManager(request, response);
	}

}
