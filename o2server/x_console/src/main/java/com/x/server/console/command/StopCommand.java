package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.Servers;

public class StopCommand {

	public static final String PATTERN_TEXT = "^ {0,}stop {0,}(.*)$";

	public static final Pattern PATTERN = Pattern.compile(PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

	protected StopCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> {

		String arg = matcher.group(1);

		switch (arg) {
		case "init":
			stopInitServer();
			break;
		case "application":
			stopApplicationServer();
			break;
		case "center":
			stopCenterServer();
			break;
		case "web":
			stopWebServer();
			break;
		case "storage":
			stopStorageServer();
			break;
		case "data":
			stopDataServer();
			break;
		default:
			stopAll();
			break;
		}
	};

	protected static void stopAll() {
		try {
			stopInitServer();
			stopApplicationServer();
			stopCenterServer();
			stopWebServer();
			stopStorageServer();
			stopDataServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	protected static void stopInitServer() {
		try {
			Servers.stopInitServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void stopDataServer() {
		try {
			Servers.stopDataServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void stopStorageServer() {
		try {
			Servers.stopStorageServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void stopApplicationServer() {
		try {
			Servers.stopApplicationServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void stopCenterServer() {
		try {
			Servers.stopCenterServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void stopWebServer() {
		try {
			Servers.stopWebServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
