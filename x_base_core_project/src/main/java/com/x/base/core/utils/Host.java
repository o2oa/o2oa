package com.x.base.core.utils;

import org.apache.commons.lang3.StringUtils;

public class Host {
	public static String ROLLBACK_IPV4 = "127.0.0.1";
	public static String ROLLBACK_LOCALHOST = "localhost";
	public static String ROLLBACK_IPV6 = "[::1]";
//	public static Integer APPLICATION_PORT = 20020;
//	public static Integer CENTER_PORT = 20030;
//	public static Integer WEB_PORT = 80;
//	public static Integer DATA_PORT = 20040;
//	public static Integer STORAGE_PORT = 20050;

	public static String httpHost(String host, Integer port, String defaultHost, Integer defaultPort) {
		String str = "http://";
		str += StringUtils.isNotEmpty(host) ? host : defaultHost;
		Integer p = (null != port) ? port : defaultPort;
		str += (p == 80) ? "" : (":" + p);
		return str;
	}

	public static Boolean isRollback(String host) {
		if (StringUtils.equalsIgnoreCase(host, ROLLBACK_IPV4)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(host, ROLLBACK_LOCALHOST)) {
			return true;
		}
		return false;
	}

}
