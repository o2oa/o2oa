package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import com.x.server.console.Main;
import com.x.server.console.ResourceFactory;

public class ExitCommand extends StopCommand {

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
				e.printStackTrace();
			}
		}
		ResourceFactory.destory();
		System.exit(0);
	}

}
