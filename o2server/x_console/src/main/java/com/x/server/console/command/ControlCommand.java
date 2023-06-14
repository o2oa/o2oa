package com.x.server.console.command;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import com.x.base.core.project.tools.StringTools;
import com.x.server.console.action.ActionControl;

public class ControlCommand {

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
			e.printStackTrace();
		}
	}

}
