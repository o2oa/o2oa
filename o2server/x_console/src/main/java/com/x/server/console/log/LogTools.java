package com.x.server.console.log;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.project.config.Config;

public class LogTools {

	public static final String DEFAULTLOGLEVEL = "org.slf4j.simpleLogger.defaultLogLevel";

	public static void setSlf4jSimple() throws Exception {
		String level = "warn";
		if (StringUtils.isNotEmpty(Config.currentNode().getLogLevel())) {
			level = Config.currentNode().getLogLevel();
		}
		// "trace", "debug", "info", "warn", "error" or "off"
		if (StringUtils.equalsIgnoreCase(level, "trace")) {
			// org.slf4j.simpleLogger.defaultLogLevel
			System.setProperty(DEFAULTLOGLEVEL, "trace");
		} else if (StringUtils.equalsIgnoreCase(level, "debug")) {
			System.setProperty(DEFAULTLOGLEVEL, "debug");
		} else if (StringUtils.equalsIgnoreCase(level, "warn")) {
			System.setProperty(DEFAULTLOGLEVEL, "warn");
		} else if (StringUtils.equalsIgnoreCase(level, "error")) {
			System.setProperty(DEFAULTLOGLEVEL, "error");
		} else if (StringUtils.equalsIgnoreCase(level, "off")) {
			System.setProperty(DEFAULTLOGLEVEL, "off");
		} else {
			System.setProperty(DEFAULTLOGLEVEL, "info");
		}
	}

	public static void configLog4j2() throws IOException {
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@");
//			ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
//			AppenderComponentBuilder console = builder.newAppender("stdout", "Console");
//			builder.add(console);
//			AppenderComponentBuilder file = builder.newAppender("log", "File");
//			file.addAttribute("fileName", "log/logging.log");
//			builder.add(file);
//			LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
//			standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
		//
//			console.add(standard);
//			file.add(standard);
//			builder.writeXmlConfiguration(System.out);
//			Configurator.initialize(builder.build());
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

		// builder.setStatusLevel(Level.DEBUG);
		// naming the logger configuration
		builder.setConfigurationName("DefaultLogger");

		// create a console appender
		AppenderComponentBuilder appenderBuilder = builder.newAppender("Console", "CONSOLE").addAttribute("target",
				ConsoleAppender.Target.SYSTEM_OUT);
		// add a layout like pattern, json etc
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d %p %c [%t] %m%n"));
		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.WARN);
		rootLogger.add(builder.newAppenderRef("Console"));

		builder.add(appenderBuilder);
		builder.add(rootLogger);

		String filename = "app.log";
		String pattern = "%d %p %c [%t] %m%n";

		builder.setStatusLevel(Level.DEBUG);
		builder.setConfigurationName("DefaultRollingFileLogger");
		// specifying the pattern layout
		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern", pattern);
		// specifying the policy for rolling file
		ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
				.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "1KB"));

		// create a console appender
		AppenderComponentBuilder rollingFileAppenderBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
				.addAttribute("fileName", filename)
				.addAttribute("filePattern", filename + "-%d{MM-dd-yy-HH-mm-ss}.log.").add(layoutBuilder)
				.addComponent(triggeringPolicy);

		builder.add(rollingFileAppenderBuilder);
		rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
		builder.add(rootLogger);
		Configurator.reconfigure(builder.build());
		Logger LOGGER = LoggerFactory.getLogger(LogTools.class);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!LogTools");
		LOGGER.error("aaaaa!!!!!!!!!!!!!!!!!!!!!!" + LOGGER.getClass().getName());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!LogTools");
		// System.setOut(IoBuilder.forLogger(LogManager.getLogger("system.out")).setLevel(Level.INFO).buildPrintStream());
	}
}
