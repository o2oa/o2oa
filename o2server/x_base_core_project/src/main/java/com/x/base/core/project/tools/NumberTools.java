package com.x.base.core.project.tools;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class NumberTools {

	public static final Pattern PERCENTSTRING_REGEX = Pattern.compile("^(-?\\d+)(\\.\\d+)?%$");

	public static Boolean valueEuqals(Integer x, Integer y) {
		if (null == x || null == y) {
			return false;
		}
		return NumberUtils.compare(x, y) == 0;
	}

	public static Boolean greaterThan(Integer x, Integer y) {
		if (null == x || null == y) {
			return false;
		}
		return x > y;
	}

	public static Integer valueEuqalsThan(Integer x, Integer y, Integer euqalsValue, Integer notEuqalsValue) {
		if (valueEuqals(x, y)) {
			return euqalsValue;
		} else {
			return notEuqalsValue;
		}
	}

	public static boolean stirngOfPercent(String str) {
		return PERCENTSTRING_REGEX.matcher(str).find();
	}

	public static Double ofDouble(String str) {
		if (NumberUtils.isCreatable(str)) {
			return NumberUtils.toDouble(str);
		} else if (stirngOfPercent(str)) {
			return NumberUtils.toDouble(StringUtils.replace(str, "%", "")) / 100;
		} else if (StringUtils.endsWithIgnoreCase(str, "千万")) {
			String _s = StringUtils.replace(str, "千万", "");
			if (NumberUtils.isParsable(_s)) {
				return NumberUtils.toDouble(_s) * 10000000;
			}
		} else if (StringUtils.endsWithIgnoreCase(str, "百万")) {
			String _s = StringUtils.replace(str, "百万", "");
			if (NumberUtils.isParsable(_s)) {
				return NumberUtils.toDouble(_s) * 1000000;
			}
		} else if (StringUtils.endsWithIgnoreCase(str, "亿")) {
			String _s = StringUtils.replace(str, "亿", "");
			if (NumberUtils.isParsable(_s)) {
				return NumberUtils.toDouble(_s) * 100000000;
			}
		} else if (StringUtils.endsWithIgnoreCase(str, "万")) {
			String _s = StringUtils.replace(str, "万", "");
			if (NumberUtils.isParsable(_s)) {
				return NumberUtils.toDouble(_s) * 10000;
			}
		} else if (StringUtils.endsWithIgnoreCase(str, "千")) {
			String _s = StringUtils.replace(str, "千", "");
			if (NumberUtils.isParsable(_s)) {
				return NumberUtils.toDouble(_s) * 1000;
			}
		} else if (StringUtils.endsWithIgnoreCase(str, "百")) {
			String _s = StringUtils.replace(str, "百", "");
			if (NumberUtils.isParsable(_s)) {
				return NumberUtils.toDouble(_s) * 100;
			}
		}
		return null;
	}

	public static boolean nullOrLessThan(Integer value, Number number) {
		if (value == null) {
			return true;
		}
		return value < number.intValue();
	}

}
