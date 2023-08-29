package com.x.base.core.project.jaxrs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.FilterTools;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;

/**
 * 判断当前用户，如果当前用户没有登陆也可以访问
 */
public abstract class AnonymousJaxrsFilter extends TokenFilter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			httpRequestCheck(request);
			FilterTools.allow(request, response);
			if (!request.getMethod().equalsIgnoreCase("options")) {
				HttpToken httpToken = new HttpToken();
				EffectivePerson effectivePerson = httpToken.who(request, response, Config.token().getCipher());
				if (!TokenType.anonymous.equals(effectivePerson.getTokenType())) {
					/** 需要自己标志500 */
					response.setStatus(500);
					response.setHeader("Content-Type", "application/json;charset=UTF-8");
					response.getWriter().write(FilterTools.APPLICATION_NOT_ANONYMOUS_JSON);
				} else {
					chain.doFilter(request, response);
				}
			} else {
				options(request,response);
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
