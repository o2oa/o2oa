package com.x.server.console;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.command.ControlCommand;
import com.x.server.console.command.ExitCommand;
import com.x.server.console.command.HelpCommand;
import com.x.server.console.command.RestartCommand;
import com.x.server.console.command.SetPasswordCommand;
import com.x.server.console.command.StartCommand;
import com.x.server.console.command.StopCommand;
import com.x.server.console.command.VersionCommand;

public class Commands {

	private Commands() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);

	private static final List<Pair<Pattern, Consumer<Matcher>>> PATTERN_COMMANDS = Arrays.asList(
			Pair.of(CommandFactory.start_pattern, StartCommand.consumer()),
			Pair.of(CommandFactory.stop_pattern, StopCommand.consumer()),
			Pair.of(CommandFactory.help_pattern, HelpCommand.consumer()),
			Pair.of(CommandFactory.version_pattern, VersionCommand.consumer()),
			Pair.of(CommandFactory.control_pattern, ControlCommand.consumer()),
			Pair.of(CommandFactory.setPassword_pattern, SetPasswordCommand.consumer()),
			Pair.of(CommandFactory.restart_pattern, RestartCommand.consumer()),
			Pair.of(CommandFactory.exit_pattern, ExitCommand.consumer()));

	public static void execute(String cmd) {
		new Thread(() -> {
			Optional<Pair<Matcher, Consumer<Matcher>>> opt = PATTERN_COMMANDS.stream()
					.map(p -> Pair.of(p.first().matcher(cmd), p.second())).filter(p -> p.first().matches()).findFirst();
			if (opt.isPresent()) {
				opt.get().second().accept(opt.get().first());
			} else {
				LOGGER.print("unknown command: {}", cmd);
			}
		}).start();
	}

}
