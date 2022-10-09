package com.x.general.assemble.control.servlet.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.ProxyServlet;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@WebServlet(urlPatterns = "/servlet/proxy/*", asyncSupported = true)
public class ActionDo extends ProxyServlet {

	private static final long serialVersionUID = -2607395779516788482L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDo.class);

	@Override
	protected String rewriteTarget(HttpServletRequest clientRequest) {
		String url = clientRequest.getParameter("url");
		try {
			if (StringUtils.isEmpty(url)) {
				// java8不支持tandardCharsets.UTF_8对象作为参数的decode
				url = URLDecoder.decode(clientRequest.getRequestURL().toString(), StandardCharsets.UTF_8.name());
				url = StringUtils.substringAfter(url, "/servlet/proxy/");
				String query = clientRequest.getQueryString();
				if (StringUtils.isEmpty(query)) {
					url = url + "?" + query;
				}
				return url;
			} else {
				// java8不支持tandardCharsets.UTF_8对象作为参数的decode
				return URLDecoder.decode(url, StandardCharsets.UTF_8.name());
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}

}