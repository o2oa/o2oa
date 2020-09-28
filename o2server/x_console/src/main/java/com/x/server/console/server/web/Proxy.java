package com.x.server.console.server.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.AsyncProxyServlet;

public class Proxy extends AsyncProxyServlet {

	private static final long serialVersionUID = 2737360000716631564L;

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String parameter = request.getQueryString();
		return target(url, parameter, this.getServletConfig().getInitParameter("port"));
	}

	private String target(String url, String parameter, String port) {
		int x = StringUtils.indexOf(url, ":", 8);
		int y = StringUtils.indexOf(url, "/", 8);
		if ((x > 0) && (y > 0)) {
			return url.substring(0, x) + port(url, port) + url.substring(y)
					+ (StringUtils.isBlank(parameter) ? "" : "?" + parameter);
		} else if (y > 0) {
			return url.substring(0, y) + port(url, port) + url.substring(y)
					+ (StringUtils.isBlank(parameter) ? "" : "?" + parameter);
		} else {
			return null;
		}
	}

	private String port(String url, String port) {
		if (StringUtils.startsWithIgnoreCase(url, "https://") || StringUtils.startsWithIgnoreCase(url, "wss://")) {
			if (StringUtils.equals(port, "443")) {
				return "";
			}
		} else if (StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils.startsWithIgnoreCase(url, "ws://")) {
			if (StringUtils.equals(port, "80")) {
				return "";
			}
		}
		return ":" + port;
	}

}
