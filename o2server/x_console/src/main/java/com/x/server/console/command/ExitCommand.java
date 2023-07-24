package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.Main;
import com.x.server.console.ResourceFactory;

public class ExitCommand extends StopCommand {

	public static final String PATTERN_TEXT = "^ {0,}exit {0,}$";

	public static final Pattern PATTERN = Pattern.compile(PATTERN_TEXT, Pattern.CASE_INSENSITIVE);
	
	private ExitCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ExitCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> exit();

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void exit() {
		stopAll();
		if (null != Main.getNodeAgent()) {
			try {
				Main.getNodeAgent().stopAgent();
				Main.getNodeAgent().interrupt();
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		ResourceFactory.destory();
		System.exit(0);
	}

}
