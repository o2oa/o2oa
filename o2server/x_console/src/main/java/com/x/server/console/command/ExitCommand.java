package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.Main;
import com.x.server.console.ResourceFactory;

public class ExitCommand extends StopCommand {

	private ExitCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ExitCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> exit();

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void exit() {
		stopInitServer();
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
