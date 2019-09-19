package com.x.base.core.project.logger;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.DateTools;

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

	public static void setLevel(String str) {
		if (StringUtils.equalsIgnoreCase(Logger.ERROR, str)) {
			Logger.level = Logger.ERROR_INT;
		}
		if (StringUtils.equalsIgnoreCase(Logger.WARN, str)) {
			Logger.level = Logger.WARN_INT;
		}
		if (StringUtils.equalsIgnoreCase(Logger.INFO, str)) {
			Logger.level = Logger.INFO_INT;
		}
		if (StringUtils.equalsIgnoreCase(Logger.DEBUG, str)) {
			Logger.level = Logger.DEBUG_INT;
		}
		if (StringUtils.equalsIgnoreCase(Logger.TRACE, str)) {
			Logger.level = Logger.TRACE_INT;
		}
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

	public static void print(Class<?> cls, String message, Object... os) {
		StringBuilder o = new StringBuilder();
		o.append(DateTools.now()).append(" ").append(LoggerFactory.class.getSimpleName());
		o.append(" [").append(Thread.currentThread().getName()).append("] ");
		o.append(cls.getName()).append(" - ");
		o.append(MessageFormatter.arrayFormat(message, os).getMessage());
		System.out.println(o.toString());
	}

	public static void print(String message, Object... os) {
		print(LoggerFactory.class, message, os);
	}

}