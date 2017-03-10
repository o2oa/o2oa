package com.x.server.console.log;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.server.Config;

public class LogTools {
	public static void setSlf4jSimple() throws Exception {
		String level = "warn";
		if (StringUtils.isNotEmpty(Config.currentNode().getLogLevel())) {
			level = Config.currentNode().getLogLevel();
		}
		// "trace", "debug", "info", "warn", "error" or "off"
		if (StringUtils.equalsIgnoreCase(level, "trace")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		} else if (StringUtils.equalsIgnoreCase(level, "debug")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
		} else if (StringUtils.equalsIgnoreCase(level, "warn")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
		} else if (StringUtils.equalsIgnoreCase(level, "error")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
		} else if (StringUtils.equalsIgnoreCase(level, "off")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
		} else {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
		}
	}
}