package com.x.server.console.command;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.plus.jndi.Resource;

import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.BaseTools;
import com.x.server.console.server.Servers;

public class StartCommand {

	private StartCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> {

		switch (matcher.group(1)) {
		case "application":
			startApplicationServer();
			break;
		case "center":
			startCenterServer();
			break;
		case "web":
			startWebServer();
			break;
		case "storage":
			startStorageServer();
			break;
		case "data":
			startDataServer();
			break;
		default:
			startAll();
			break;
		}
	};

	private static void initIfNecessary() throws Exception {
		JsonObject jsonObject = BaseTools.readConfigObject(Config.PATH_CONFIG_TOKEN, JsonObject.class);
		String value = XGsonBuilder.extractString(jsonObject, "password");
		if (StringUtils.isBlank(value)) {
			Servers.startInitServer();
			// 等待停止信号
			LinkedBlockingQueue<String> stopSignalQueue = new LinkedBlockingQueue<>();
			new Resource(Config.RESOURCE_INITSERVERSTOPSIGNAL, stopSignalQueue);
			stopSignalQueue.take();
			Servers.stopInitServer();
		}
	}

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void startApplicationServer() {
		try {
			initIfNecessary();
			Servers.startApplicationServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void startCenterServer() {
		try {
			initIfNecessary();
			Servers.startCenterServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void startWebServer() {
		try {
			initIfNecessary();
			Servers.startWebServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void startStorageServer() {
		try {
			Servers.startStorageServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void startDataServer() {
		try {
			if (BooleanUtils.isNotTrue(Config.externalDataSources().enable())) {
				// 如果启用了外部数据源,那么不启用默认数据库.
				Servers.startDataServer();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	public static void startAll() {
		startDataServer();
		startStorageServer();
		startCenterServer();
		startApplicationServer();
		startWebServer();
	}

}
