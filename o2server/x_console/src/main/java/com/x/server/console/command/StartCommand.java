package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.server.console.server.Servers;

public class StartCommand {

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

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void startApplicationServer() {
		try {
			if (BooleanUtils.isTrue(Servers.applicationServerIsRunning())) {
				System.out.println("application server is running.");
			} else {
				Servers.startApplicationServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startCenterServer() {
		try {
			if (BooleanUtils.isTrue(Servers.centerServerIsRunning())) {
				System.out.println("center server is running.");
			} else {
				Servers.startCenterServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startWebServer() {
		try {
			if (BooleanUtils.isTrue(Servers.webServerIsRunning())) {
				System.out.println("web server is running.");
			} else {
				Servers.startWebServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startStorageServer() {
		try {
			if (BooleanUtils.isTrue(Servers.storageServerIsRunning())) {
				System.out.println("storage server is running.");
			} else if (BooleanUtils.isNotTrue(Config.externalStorageSources().getEnable())) {
				// 如果启用了外部数据源,那么不启用默认文件服务器.
				Servers.startStorageServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startDataServer() {
		try {
			if (BooleanUtils.isTrue(Servers.dataServerIsRunning())) {
				System.out.println("data server is running.");
			} else if (BooleanUtils.isNotTrue(Config.externalDataSources().enable())) {
				// 如果启用了外部数据源,那么不启用默认数据库.
				Servers.startDataServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startAll() {
		try {
			DataServer dataServer = Config.currentNode().getData();
			if ((null != dataServer) && (BooleanUtils.isTrue(dataServer.getEnable()))) {
				startDataServer();
			}

			StorageServer storageServer = Config.currentNode().getStorage();
			if ((null != storageServer) && (BooleanUtils.isTrue(storageServer.getEnable()))) {
				startStorageServer();
			}

			CenterServer centerServer = Config.currentNode().getCenter();
			if ((null != centerServer) && (BooleanUtils.isTrue(centerServer.getEnable()))) {
				startCenterServer();
			}
			ApplicationServer applicationServer = Config.currentNode().getApplication();
			if ((null != applicationServer) && (BooleanUtils.isTrue(applicationServer.getEnable()))) {
				startApplicationServer();
			}
			WebServer webServer = Config.currentNode().getWeb();
			if ((null != webServer) && (BooleanUtils.isTrue(webServer.getEnable()))) {
				startWebServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
