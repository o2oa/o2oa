package com.x.base.core.project.jaxrs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HttpHeaders;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.FilterTools;
import com.x.base.core.project.http.HttpToken;

/**
 * 必须由前台已经登陆的用户访问
 * @author sword
 */
public abstract class AnonymousCipherManagerUserJaxrsFilter extends TokenFilter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			httpRequestCheck(request);
			FilterTools.allow(request, response);
			if (!request.getMethod().equalsIgnoreCase(HTTP_OPTIONS)) {
				HttpToken httpToken = new HttpToken();
				httpToken.whoNotRefreshToken(request, response, Config.token().getCipher());
				chain.doFilter(request, response);
			} else {
				options(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
	}
}
