package com.x.base.core.utils;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class StringTools {
	public static int utf8Length(String str) {
		if (StringUtils.isEmpty(str)) {
			return 0;
		}
		return str.getBytes(Charset.forName("UTF-8")).length;
	}

	public static String utf8SubString(String str, int len) {
		if (len < 0 || StringUtils.isEmpty(str) || utf8Length(str) <= len) {
			return str;
		}
		byte[] bs = ArrayUtils.subarray(str.getBytes(Charset.forName("UTF-8")), 0, len);
		String cut = new String(bs, Charset.forName("UTF-8"));
		if (!cut.isEmpty()) {
			cut = cut.substring(0, cut.length() - 1);
		}
		for (int i = cut.length(); i < str.length(); i++) {
			String buf = cut + str.charAt(i);
			if (utf8Length(buf) == len) {
				return buf;
			} else if (utf8Length(buf) > len) {
				return cut;
			} else {
				cut = buf;
			}
		}
		return "";
	}

	public static String check(String str, int min, int max, boolean cutoff) throws Exception {
		int len = utf8Length(str);
		if (min > len) {
			throw new Exception(str + " length is (" + len + "), is too short, min (" + min + "), max (" + max + ").");
		}
		if (len <= max) {
			return str;
		} else {
			if (cutoff == true) {
				return utf8SubString(str, max);
			} else {
				throw new Exception(str + " length is (" + len + "), min (" + min + "), max (" + max + ").");
			}
		}
	}

	public static String uniqueToken() {
		return UUID.randomUUID().toString();
	}

	public static String random6() {
		String str = UUID.randomUUID().toString();
		return StringUtils.substring(str, 0, 6);
	}

	public static String random4() {
		String str = UUID.randomUUID().toString();
		return StringUtils.substring(str, 0, 4);
	}

	public static String toString(Object obj, String propertyName) throws Exception {
		Object o = obj;
		if (null != obj) {
			o = PropertyUtils.getProperty(o, propertyName);
		}
		return Objects.toString(o);
	}

	public static boolean isMobile(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^1[3-9]\\d{9}$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}
}