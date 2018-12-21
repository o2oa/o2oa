package com.x.server.console;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class OptionFactory {

	public static Options options() {
		Options ops = new Options();
		ops.addOption(new Option("help", "print usage message."));
		ops.addOption(start());
		ops.addOption(exit());
		return ops;
	}

	private static Option start() {
		Option o = Option.builder("start").hasArg().argName("center|storage|data|web|application|all")
				.desc("start server or start all server alone.").build();
		return o;
	}

	private static Option exit() {
		Option o = Option.builder("exit").desc("stop all server and exit.").build();
		return o;
	}

}
