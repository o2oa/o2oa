package com.x.base.core.project.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Host {
	public static String ROLLBACK_IPV4 = "127.0.0.1";
	public static String ROLLBACK_LOCALHOST = "localhost";
	public static String ROLLBACK_IPV6 = "[::1]";
	// public static Integer APPLICATION_PORT = 20020;
	// public static Integer CENTER_PORT = 20030;
	// public static Integer WEB_PORT = 80;
	// public static Integer DATA_PORT = 20040;
	// public static Integer STORAGE_PORT = 20050;

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

	public static boolean innerIp(String ip) {
		// // 匹配10.0.0.0 - 10.255.255.255的网段
		// String pattern_10 =
		// "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3}";
		//
		// // 匹配172.16.0.0 - 172.31.255.255的网段
		// String pattern_172 =
		// "172\\.([1][6-9]|[2]\\d|3[01])(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}";
		//
		// // 匹配192.168.0.0 - 192.168.255.255的网段
		// String pattern_192 =
		// "192\\.168(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}";
		String pattern = "((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))"
				+ "(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|"
				+ "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})";
		Pattern reg = Pattern.compile(pattern);
		Matcher match = reg.matcher(ip);
		return match.find();
	}

	public static boolean ip(String addr) {
		if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);

		Matcher mat = pat.matcher(addr);

		return mat.find();
	}

}
