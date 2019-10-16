package com.x.processplatform.core.express.query;

import org.apache.commons.lang3.StringUtils;

public abstract class Comparison {
	private static String[] equals = new String[] { "equals", "=" };
	private static String[] notEquals = new String[] { "notEquals", "!=", "<>" };
	private static String[] greaterThan = new String[] { "greaterThan", ">" };
	private static String[] greaterThanOrEqualTo = new String[] { "greaterThanOrEqualTo", ">=" };
	private static String[] lessThan = new String[] { "lessThan", "<" };
	private static String[] lessThanOrEqualTo = new String[] { "lessThanOrEqualTo", "<=" };
	private static String[] like = new String[] { "like" };
	private static String[] notLike = new String[] { "notLike" };

	public static boolean isEquals(String comparison) throws Exception {
		for (String str : equals) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotEquals(String comparison) throws Exception {
		for (String str : notEquals) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isGreaterThan(String comparison) throws Exception {
		for (String str : greaterThan) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isGreaterThanOrEqualTo(String comparison) throws Exception {
		for (String str : greaterThanOrEqualTo) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLessThan(String comparison) throws Exception {
		for (String str : lessThan) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLessThanOrEqualTo(String comparison) throws Exception {
		for (String str : lessThanOrEqualTo) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLike(String comparison) throws Exception {
		for (String str : like) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotLike(String comparison) throws Exception {
		for (String str : notLike) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}
}