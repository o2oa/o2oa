package com.x.base.core.utils;

import org.apache.commons.lang3.math.NumberUtils;

public class NumberTools {

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

}
