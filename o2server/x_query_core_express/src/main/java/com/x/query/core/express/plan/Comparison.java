package com.x.query.core.express.plan;

import org.apache.commons.lang3.StringUtils;

public abstract class Comparison {
	private static String[] equals = new String[] { "equals", "=" };
	private static String[] notEquals = new String[] { "notEquals", "!=", "<>" };
	private static String[] greaterThan = new String[] { "greaterThan", ">" };
	private static String[] greaterThanOrEqualTo = new String[] { "greaterThanOrEqualTo", ">=" };
	private static String[] lessThan = new String[] { "lessThan", "<" };
	private static String[] lessThanOrEqualTo = new String[] { "lessThanOrEqualTo", "<=" };
	private static String[] like = new String[] { "like" };
	private static String[] notLike = new String[] { "notLike", "not like" };
	private static String[] between = new String[] { "range", "between" };
	private static String[] isMember = new String[] { "isMember", "in" };
	private static String[] listLike = new String[] { "listLike" };

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

	public static boolean isBetween(String comparison) throws Exception {
		for (String str : between) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isIsMember(String comparison) throws Exception {
		for (String str : isMember) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isListLike(String comparison) throws Exception {
		for (String str : listLike) {
			if (StringUtils.equalsIgnoreCase(str, StringUtils.trim(comparison))) {
				return true;
			}
		}
		return false;
	}

	public static String getMatchCom(String comparison) throws Exception {
		if(isNotEquals(comparison)){
			return notEquals[notEquals.length-1];

		}else if(isGreaterThan(comparison)){
			return greaterThan[greaterThan.length-1];

		}else if(isGreaterThanOrEqualTo(comparison)){
			return greaterThanOrEqualTo[greaterThanOrEqualTo.length-1];

		}else if(isLessThan(comparison)){
			return lessThan[lessThan.length-1];

		}else if(isLessThanOrEqualTo(comparison)){
			return lessThanOrEqualTo[lessThanOrEqualTo.length-1];

		}else if(isLike(comparison)){
			return like[like.length-1];

		}else if(isNotLike(comparison)){
			return notLike[notLike.length-1];

		}else if(isIsMember(comparison)){
			return isMember[isMember.length-1];

		}else{
			return equals[equals.length-1];
		}
	}
}
