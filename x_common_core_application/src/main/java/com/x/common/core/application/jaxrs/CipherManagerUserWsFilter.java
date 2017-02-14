package com.x.common.core.application.jaxrs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.FilterTools;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.common.core.application.AbstractThisApplication;

/**
 * 必须由前台已经登陆的用户访问
 */
public abstract class CipherManagerUserWsFilter extends TokenFilter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			if (!AbstractThisApplication.initialized) {
				response.getWriter().write(FilterTools.Application_Not_Initialized_Json);
				return;
			}
			FilterTools.allow(request, response);
			if (!request.getMethod().equalsIgnoreCase("options")) {
				HttpToken httpToken = new HttpToken();
				EffectivePerson effectivePerson = httpToken.who(request, response, this.getTokenKey());

				if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
					response.setHeader("Content-Type", "application/json;charset=UTF-8");
					response.getWriter().write(FilterTools.Application_Not_CipherManagerUser_Json);
				} else {
					request.getSession().setAttribute(HttpToken.X_Person, effectivePerson);
					request.getSession().setAttribute(HttpAttribute.x_remoteAddress, request.getRemoteAddr());
					chain.doFilter(request, response);
				}
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
