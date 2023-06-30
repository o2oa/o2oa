package com.x.server.console.command;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
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
import com.x.base.core.project.tools.H2Tools;
import com.x.server.console.server.Servers;

public class StartCommand {

	private StartCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> {

		if (initIfNecessary(StringUtils.equalsIgnoreCase(matcher.group(1), "init"))) {
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
		}
	};

	private static boolean initIfNecessary(boolean force) {
		try {
			if (force || initIfNecessarySetPassword() || initIfNecessaryUpgradeLocalRepositoryDataH2()) {
				Servers.startInitServer();
				// 等待停止信号
				LinkedBlockingQueue<String> stopSignalQueue = new LinkedBlockingQueue<>();
				new Resource(Config.RESOURCE_INITSERVERSTOPSIGNAL, stopSignalQueue);
				stopSignalQueue.take();
				Servers.stopInitServer();
			}
			return true;
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			LOGGER.error(ie);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	private static boolean initIfNecessarySetPassword() throws Exception {
		JsonObject jsonObject = BaseTools.readConfigObject(Config.PATH_CONFIG_TOKEN, JsonObject.class);
		String value = XGsonBuilder.extractString(jsonObject, "password");
		return StringUtils.isBlank(value);
	}

	private static boolean initIfNecessaryUpgradeLocalRepositoryDataH2() throws IOException, URISyntaxException {
		Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
		if (Files.exists(path)) {
			Optional<String> jarVersion = H2Tools.jarVersion();
			Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
			return ((jarVersion.isPresent() && localRepositoryDataH2Version.isPresent())
					&& (!StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get())));
		}
		return false;
	}

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void startApplicationServer() {
		try {
			Servers.startApplicationServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void startCenterServer() {
		try {
			Servers.startCenterServer();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void startWebServer() {
		try {
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
