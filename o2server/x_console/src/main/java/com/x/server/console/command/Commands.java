package com.x.server.console.command;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class Commands {

	private Commands() {
		// nothing
	}

	public static final String  COMMANDTERMINATEDSIGNAL_SUCCESS = "success";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);

	private static final List<Pair<Pattern, Consumer<Matcher>>> PATTERN_COMMANDS = Arrays.asList(
			Pair.of(StartCommand.PATTERN, StartCommand.consumer()),
			Pair.of(StopCommand.PATTERN, StopCommand.consumer()), Pair.of(HelpCommand.PATTERN, HelpCommand.consumer()),
			Pair.of(VersionCommand.PATTERN, VersionCommand.consumer()),
			Pair.of(ControlCommand.PATTERN, ControlCommand.consumer()),
			Pair.of(SetPasswordCommand.PATTERN, SetPasswordCommand.consumer()),
			Pair.of(RestartCommand.PATTERN, RestartCommand.consumer()),
			Pair.of(ExitCommand.PATTERN, ExitCommand.consumer()));

	public static void execute(String cmd) {
		new Thread(() -> {
			Optional<Pair<Matcher, Consumer<Matcher>>> opt = PATTERN_COMMANDS.stream()
					.map(p -> Pair.of(p.first().matcher(cmd), p.second())).filter(p -> p.first().matches()).findFirst();
			if (opt.isPresent()) {
				opt.get().second().accept(opt.get().first());
			} else {
				LOGGER.print("unknown command: {}", cmd);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error(e);
			}
		}).start();
	}

}
