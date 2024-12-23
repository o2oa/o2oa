package com.x.base.core.project.jaxrs;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class DenialOfServiceFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setHeader("Content-Type", "text/html;charset=utf-8");
		response.getWriter()
				.write("<html><body><h2>HTTP ERROR 404 Not Found</h2></body></html>");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// nothing
	}

	@Override
	public void destroy() {
		// nothing
	}

}
