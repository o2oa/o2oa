package com.x.general.assemble.control.servlet.proxy;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.ProxyServlet;

@WebServlet(urlPatterns = "/servlet/proxy/*", asyncSupported = true)
public class ActionDo extends ProxyServlet {

	private static final long serialVersionUID = -2607395779516788482L;

	@Override
	protected String rewriteTarget(HttpServletRequest clientRequest) {
		String url = clientRequest.getParameter("url");
		if (StringUtils.isEmpty(url)) {
			url = URLDecoder.decode(clientRequest.getRequestURL().toString(), StandardCharsets.UTF_8);
			url = StringUtils.substringAfter(url, "/servlet/proxy/");
			String query = clientRequest.getQueryString();
			if (StringUtils.isEmpty(query)) {
				url = url + "?" + query;
			}
			return url;
		} else {
			return URLDecoder.decode(url, StandardCharsets.UTF_8);

		}
	}

}