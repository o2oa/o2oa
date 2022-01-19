package com.x.server.console.log;

import java.io.PrintStream;
import java.util.Map;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.LoggerFactory;

public class Log4j2Configuration {

	private Log4j2Configuration() {

	}

	private static final String CONVERSION_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %p %c - %m%n";

	public static void reconfigure() {

		final PrintStream stderr = System.err;
		final PrintStream stdout = System.out;

		final LoggerContext context = LoggerContext.getContext(false);
		final Configuration config = context.getConfiguration();

		addStandardOutAppender(config, stdout);

		bypassErr(stderr);
		bypassOut(stdout);

		context.updateLoggers();
	}

	private static void addStandardOutAppender(final Configuration config, final PrintStream stdout) {
		final LoggerConfig rootLogger = config.getRootLogger();
		final Map<String, Appender> appenders = rootLogger.getAppenders();
		//System.out.println("log4j appenders: " + appenders);
		if  (appenders.containsKey("stdout")) {
			return;
		}
		final PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config)
				.withPattern(CONVERSION_PATTERN).build();
		final Appender appender = OutputStreamAppender.createAppender(layout, null, stdout, "stdout", true, true);
		appender.start();
		config.addAppender(appender);
		rootLogger.addAppender(appender, null, null);
	}

	private static void bypassErr(final PrintStream stderr) {
		BypassLoggerPrintStream printStream = new BypassLoggerPrintStream(stderr,
				LoggerFactory.getLogger("System.err"));
		System.setErr(printStream);
	}

	private static void bypassOut(final PrintStream stdout) {
		BypassLoggerPrintStream printStream = new BypassLoggerPrintStream(stdout,
				LoggerFactory.getLogger("System.out"));
		System.setOut(printStream);
	}

}
