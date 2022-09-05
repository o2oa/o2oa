package com.x.base.core.project.tools;

import org.apache.commons.lang3.StringUtils;

public class SqlTools {

	private SqlTools() {
		// nothing
	}

	public static String removeLikePatternEscapeCharacter(String query) {
		return StringUtils.remove(StringUtils.remove(StringUtils.remove(query, "%"), "_"), "\\");
	}

}
