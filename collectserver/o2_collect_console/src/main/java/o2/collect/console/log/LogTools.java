package o2.collect.console.log;

import org.apache.commons.lang3.StringUtils;

public class LogTools {
	public static void setSlf4jSimple() throws Exception {
		String level = "info";
		// if (StringUtils.isNotEmpty(Config.getLogLevel())) {
		// level = Config.getLogLevel();
		// }
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