package com.x.server.console.command;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.server.console.server.Servers;

public class StartCommand {

	private static final String PATTERN_TEXT = "^ {0,}start {0,}(data|storage|center|application|web|all|admin|) {0,}$";

	public static final Pattern PATTERN = Pattern.compile(PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

	private StartCommand() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

	private static final Consumer<Matcher> consumer = matcher -> {
		try {
			String arg = matcher.group(1);
			if (!StringUtils.endsWithIgnoreCase(arg, "skipAdmin")
					&& (StringUtils.equalsIgnoreCase(arg, "admin") || ifAdminServerNecessary())) {
				startAdminServer();
			}
			if (StringUtils.equalsIgnoreCase(arg, "application")
					|| StringUtils.equalsIgnoreCase(arg, "applicationSkipAdmin")) {
				startApplicationServer();
			} else if (StringUtils.equalsIgnoreCase(arg, "center")
					|| StringUtils.equalsIgnoreCase(arg, "centerSkipAdmin")) {
				startCenterServer();
			} else if (StringUtils.equalsIgnoreCase(arg, "web") || StringUtils.equalsIgnoreCase(arg, "webSkipAdmin")) {
				startWebServer();
			} else if (StringUtils.equalsIgnoreCase(arg, "storage")
					|| StringUtils.equalsIgnoreCase(arg, "storageSkipAdmin")) {
				startStorageServer();
			} else if (StringUtils.equalsIgnoreCase(arg, "data")
					|| StringUtils.equalsIgnoreCase(arg, "dataSkipAdmin")) {
				startDataServer();
			} else {
				startAll();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	};

	private static boolean ifAdminServerNecessary() throws Exception {
		return (ifAdminServerNecessarySetPassword() || ifAdminServerNecessaryUpgradeLocalRepositoryDataH2());
	}

	private static boolean ifAdminServerNecessarySetPassword() throws Exception {
		JsonObject jsonObject = BaseTools.readConfigObject(Config.PATH_CONFIG_TOKEN, JsonObject.class);
		String value = XGsonBuilder.extractString(jsonObject, "password");
		return StringUtils.isBlank(value);
	}

	private static boolean ifAdminServerNecessaryUpgradeLocalRepositoryDataH2() throws IOException, URISyntaxException {
		Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
		if (Files.exists(path)) {
			Optional<String> jarVersion = H2Tools.jarVersion();
			Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
			return ((jarVersion.isPresent() && localRepositoryDataH2Version.isPresent())
					&& (!StringUtils.equalsIgnoreCase(jarVersion.get(), localRepositoryDataH2Version.get())));
		}
		return false;
	}

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void startAdminServer() {
		try {
			Servers.startAdminServer();
			// 等待停止信号
			Servers.getAdminServer().join();
			Servers.stopAdminServer();
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			LOGGER.error(ie);
		} catch (Exception e) {
			LOGGER.error(e);
		}
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
