package com.x.query.core.express.statement;

import org.apache.commons.lang3.StringUtils;

public abstract class Comparison {

	private Comparison() {
		// nothing
	}

	private static String[] equals = new String[] { "equals", "=" };
	private static String[] notEquals = new String[] { "notEquals", "!=", "<>" };
	private static String[] greaterThan = new String[] { "greaterThan", ">" };
	private static String[] greaterThanOrEqualTo = new String[] { "greaterThanOrEqualTo", ">=" };
	private static String[] lessThan = new String[] { "lessThan", "<" };
	private static String[] lessThanOrEqualTo = new String[] { "lessThanOrEqualTo", "<=" };
	private static String[] like = new String[] { "like" };
	private static String[] notLike = new String[] { "notLike" };
	private static String[] in = new String[] { "in" };

	public static boolean isEquals(String comparison) {
		for (String str : equals) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotEquals(String comparison) {
		for (String str : notEquals) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isGreaterThan(String comparison) {
		for (String str : greaterThan) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isGreaterThanOrEqualTo(String comparison) {
		for (String str : greaterThanOrEqualTo) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLessThan(String comparison) {
		for (String str : lessThan) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLessThanOrEqualTo(String comparison) {
		for (String str : lessThanOrEqualTo) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLike(String comparison) {
		for (String str : like) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotLike(String comparison) {
		for (String str : notLike) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isIn(String comparison) {
		for (String str : in) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}
}