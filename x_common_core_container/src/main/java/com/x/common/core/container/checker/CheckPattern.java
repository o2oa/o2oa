package com.x.common.core.container.checker;

import java.util.regex.Pattern;

public class CheckPattern {
	public static final Pattern simply_pattern = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9\\_\\(\\)\\-\\ \\.]*$");

	public static final Pattern fileName_pattern = Pattern.compile("[^/\\\\<>*?|\"]+(\\.?)[^/\\\\<>*?|\"]+");

	public static final Pattern mail_pattern = Pattern.compile(
			"^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern mobile_pattern = Pattern.compile("^1[3-9]\\d{9}$");
}
