package com.x.base.core.project.tools;

import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

public class NumberTools {

	private NumberTools() {
		// nothing
	}

	public static final Pattern PERCENTSTRING_REGEX = Pattern.compile("^(-?\\d+)(\\.\\d+)?%$");

	public static boolean valueEuqals(Integer x, Integer y) {
		if (null == x || null == y) {
			return false;
		}
		return NumberUtils.compare(x, y) == 0;
	}

	public static boolean greaterThan(Number x, Number y) {
		if (null == x || null == y) {
			return false;
		}
		return x.doubleValue() > y.doubleValue();
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

	public static boolean nullOrLessThan(Number value, Number number) {
		if (value == null) {
			return true;
		}
		return value.doubleValue() < number.doubleValue();
	}

	public static boolean nullOrGreaterThan(Number value, Number number) {
		if (value == null) {
			return true;
		}
		return value.doubleValue() > number.doubleValue();
	}

}
