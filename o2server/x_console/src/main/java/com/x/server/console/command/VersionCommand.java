package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.server.console.action.ActionVersion;

public class VersionCommand {

	private VersionCommand() {
		// nothing
	}

	public static final String PATTERN_TEXT = "^ {0,}version {0,}$";

	public static final Pattern PATTERN = Pattern.compile(PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

	private static final Consumer<Matcher> consumer = matcher -> {
		try {
			new ActionVersion().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

}
