package com.x.base.core.logger;

public class LoggerFactory {

	private LoggerFactory() {
	}

	public static Logger getLogger(Class<?> clz) {
		Logger logger = new Logger(clz.getName());
		return logger;
	}

	public static void setLevelTrace() {
		Logger.level = Logger.TRACE_INT;
	}

	public static void setLevelDebug() {
		Logger.level = Logger.DEBUG_INT;
	}

	public static void setLevelInfo() {
		Logger.level = Logger.INFO_INT;
	}

	public static void setLevelWarn() {
		Logger.level = Logger.WARN_INT;
	}

	public static String getLevel() {
		switch (Logger.level) {
		case Logger.TRACE_INT:
			return Logger.TRACE;
		case Logger.DEBUG_INT:
			return Logger.DEBUG;
		case Logger.WARN_INT:
			return Logger.WARN;
		default:
			return Logger.INFO;
		}
	}

}