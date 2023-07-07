package com.x.server.console.command;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.server.console.action.ActionControl;

public class ControlCommand {

	public static final String PATTERN_TEXT = "^ {0,}ctl {0,}(.*)$";

	public static final Pattern PATTERN = Pattern.compile(PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

	private ControlCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ControlCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> control(matcher.group(0));

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void control(String cmd) {
		try {
			String[] args = StringTools.translateCommandline(cmd);
			args = Arrays.copyOfRange(args, 1, args.length);
			ActionControl action = new ActionControl();
			action.execute(args);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
