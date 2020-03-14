package com.x.base.core.project.tools;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

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

}
