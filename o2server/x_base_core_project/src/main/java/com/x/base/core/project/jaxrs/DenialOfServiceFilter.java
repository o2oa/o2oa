package com.x.base.core.project.jaxrs;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.http.FilterTools;

public class DenialOfServiceFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setStatus(403);
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		response.getWriter().write(FilterTools.APPLICATION_403_JSON);
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