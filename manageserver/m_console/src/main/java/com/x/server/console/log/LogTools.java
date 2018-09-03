package com.x.server.console.log;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;

public class LogTools {
	public static void setSlf4jSimple() throws Exception {
		String level = "warn";
		if (StringUtils.isNotEmpty(Config.currentNode().getLogLevel())) {
			level = Config.currentNode().getLogLevel();
		}
		// "trace", "debug", "info", "warn", "error" or "off"
		if (StringUtils.equalsIgnoreCase(level, "trace")) {
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "trace");
		} else if (StringUtils.equalsIgnoreCase(level, "debug")) {
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
		} else if (StringUtils.equalsIgnoreCase(level, "warn")) {
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "warn");
		} else if (StringUtils.equalsIgnoreCase(level, "error")) {
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "error");
		} else if (StringUtils.equalsIgnoreCase(level, "off")) {
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "off");
		} else {
			System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
		}
	}
}