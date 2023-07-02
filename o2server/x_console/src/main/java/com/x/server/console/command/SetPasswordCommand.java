package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import com.x.server.console.action.ActionSetPassword;

public class SetPasswordCommand {

	private static final Consumer<Matcher> consumer = matcher -> setPassword(matcher.group(1), matcher.group(2));

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void setPassword(String oldPassword, String newPassword) {
		try {
			new ActionSetPassword().execute(oldPassword, newPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
