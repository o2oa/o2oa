package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.server.console.CommandFactory;
import com.x.server.console.action.ActionVersion;
import com.x.server.console.server.Servers;

public class VersionCommand {

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
