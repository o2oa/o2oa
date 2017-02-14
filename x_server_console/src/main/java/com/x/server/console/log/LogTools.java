package com.x.server.console.log;

import org.apache.commons.lang3.StringUtils;

public class LogTools {
	public static void setSlf4jLogback() {
		// LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		// PatternLayoutEncoder patternLayoutEncoder = new
		// PatternLayoutEncoder();
		// patternLayoutEncoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level
		// %logger{36} - %msg%n");
		// patternLayoutEncoder.setContext(lc);
		// patternLayoutEncoder.start();
		// ConsoleAppender<ILoggingEvent> consoleAppender = new
		// ConsoleAppender<ILoggingEvent>();
		// consoleAppender.setEncoder(patternLayoutEncoder);
		// consoleAppender.start();
		// Logger logger = (Logger)
		// LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		// logger.addAppender(consoleAppender);
		// logger.setLevel(Level.WARN);
		// logger.setAdditive(false); /* set to true if root should log too */
	}

	public static void setSlf4jSimple(String level) {
		// el ALL
		// The ALL has the lowest possible rank and is intended to turn on all
		// logging.
		// static Level DEBUG
		// The DEBUG Level designates fine-grained informational events that are
		// most useful to debug an application.
		// static Level ERROR
		// The ERROR level designates error events that might still allow the
		// application to continue running.
		// static Level FATAL
		// The FATAL level designates very severe error events that will
		// presumably lead the application to abort.
		// static Level INFO
		// The INFO level designates informational messages that highlight the
		// progress of the application at coarse-grained level.
		// static Level OFF
		if (StringUtils.equalsIgnoreCase(level, "all")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "all");
		} else if (StringUtils.equalsIgnoreCase(level, "debug")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
		} else if (StringUtils.equalsIgnoreCase(level, "error")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
		} else if (StringUtils.equalsIgnoreCase(level, "fatal")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "fatal");
		} else {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
		}

		/*
		 * org.slf4j.simpleLogger.logFile - The output target which can be the
		 * path to a file, or the special values "System.out" and "System.err".
		 * Default is "System.err". org.slf4j.simpleLogger.defaultLogLevel -
		 * Default log level for all instances of SimpleLogger. Must be one of
		 * ("trace", "debug", "info", "warn", or "error"). If not specified,
		 * defaults to "info". org.slf4j.simpleLogger.log.a.b.c - Logging detail
		 * level for a SimpleLogger instance named "a.b.c". Right-side value
		 * must be one of "trace", "debug", "info", "warn", or "error". When a
		 * SimpleLogger named "a.b.c" is initialized, its level is assigned from
		 * this property. If unspecified, the level of nearest parent logger
		 * will be used, and if none is set, then the value specified by
		 * org.slf4j.simpleLogger.defaultLogLevel will be used.
		 * org.slf4j.simpleLogger.showDateTime - Set to true if you want the
		 * current date and time to be included in output messages. Default is
		 * false org.slf4j.simpleLogger.dateTimeFormat - The date and time
		 * format to be used in the output messages. The pattern describing the
		 * date and time format is defined by SimpleDateFormat. If the format is
		 * not specified or is invalid, the number of milliseconds since start
		 * up will be output. org.slf4j.simpleLogger.showThreadName -Set to true
		 * if you want to output the current thread name. Defaults to true.
		 * org.slf4j.simpleLogger.showLogName - Set to true if you want the
		 * Logger instance name to be included in output messages. Defaults to
		 * true. org.slf4j.simpleLogger.showShortLogName - Set to true if you
		 * want the last component of the name to be included in output
		 * messages. Defaults to false. org.slf4j.simpleLogger.levelInBrackets -
		 * Should the level string be output in brackets? Defaults to false.
		 * org.slf4j.simpleLogger.warnLevelString - The string value output for
		 * the warn level. Defaults to WARN.
		 */
	}
}
