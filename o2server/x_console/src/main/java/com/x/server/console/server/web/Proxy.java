package com.x.server.console.server.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.ProxyServlet;

public class Proxy extends ProxyServlet {

	private static final long serialVersionUID = 2737360000716631564L;

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return target(url, this.getServletConfig().getInitParameter("port"));
	}

	private String target(String url, String port) {
		int x = StringUtils.indexOf(url, ":", 8);
		int y = StringUtils.indexOf(url, "/", 8);
		if ((x > 0) && (y > 0)) {
			return url.substring(0, x) + port(url, port) + url.substring(y);
		} else if (y > 0) {
			return url.substring(0, y) + port(url, port) + url.substring(y);
		} else {
			return null;
		}
	}

	private String port(String url, String port) {
		if (StringUtils.startsWithIgnoreCase(url, "https://")) {
			if (StringUtils.equals(port, "443")) {
				return "";
			}
		} else if (StringUtils.startsWithIgnoreCase(url, "http://")) {
			if (StringUtils.equals(port, "80")) {
				return "";
			}
		}
		return ":" + port;
	}

//	@Test
//	public void test1() {
//		System.out.println(target("http://www.o2oa.net:20030/111/22?1=1", "80"));
//		System.out.println(target("http://www.o2oa.net:20030/111/22?1=1", "81"));
//		System.out.println(target("https://www.o2oa.net:20030/111/22?1=1", "80"));
//		System.out.println(target("https://www.o2oa.net:20030/111/22?1=1", "443"));
//		System.out.println(target("https://www.o2oa.net:20030/111/22?1=1", "4430"));
//	}

}
