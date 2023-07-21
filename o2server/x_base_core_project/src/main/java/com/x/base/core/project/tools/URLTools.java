package com.x.base.core.project.tools;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

public class URLTools {

	public static String getQueryStringParameter(String queryString, String name) {
		String value = StringUtils.substringAfter(queryString, name + "=");
		if (StringUtils.contains(value, "&")) {
			return StringUtils.substringBefore(value, "&");
		} else if (StringUtils.contains(value, "!")) {
			return StringUtils.substringBefore(value, "!");
		} else {
			return value;
		}
	}

	/*
	 * Java8 URLEncoder.encode throw Exception,Java11 URLEncoder.encode 不抛出错误
	 * 统一这两个方法.
	 */
	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}