package com.x.server.console.command;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.server.console.server.Servers;

public class StopCommand {

	private static final Consumer<Matcher> consumer = matcher -> {
		switch (matcher.group(1)) {
		case "application":
			stopApplicationServer();
			break;
		case "center":
			stopCenterServer();
			break;
		case "web":
			stopWebServer();
			break;
		case "storage":
			stopStorageServer();
			break;
		case "data":
			stopDataServer();
			break;
		default:
			stopAll();

			break;
		}
	};

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static void stopDataServer() {
		try {
			if (BooleanUtils.isFalse(Servers.dataServerIsRunning())) {
				System.out.println("data server is not running.");
			} else {
				Servers.stopDataServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopStorageServer() {
		try {
			if (BooleanUtils.isFalse(Servers.storageServerIsRunning())) {
				System.out.println("storage server is not running.");
			} else {
				Servers.stopStorageServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopApplicationServer() {
		try {
			if (BooleanUtils.isFalse(Servers.applicationServerIsRunning())) {
				System.out.println("application server is not running.");
			} else {
				Servers.stopApplicationServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopCenterServer() {
		try {
			if (BooleanUtils.isFalse(Servers.centerServerIsRunning())) {
				System.out.println("center server is not running.");
			} else {
				Servers.stopCenterServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopWebServer() {
		try {
			if (BooleanUtils.isFalse(Servers.webServerIsRunning())) {
				System.out.println("web server is not running.");
			} else {
				Servers.stopWebServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void stopAll() {
		try {
			WebServer webServer = Config.currentNode().getWeb();
			if ((null != webServer) && (BooleanUtils.isTrue(webServer.getEnable()))) {
				stopWebServer();
			}
			ApplicationServer applicationServer = Config.currentNode().getApplication();
			if ((null != applicationServer) && (BooleanUtils.isTrue(applicationServer.getEnable()))) {
				stopApplicationServer();
			}
			stopCenterServer();
			StorageServer storageServer = Config.currentNode().getStorage();
			if ((null != storageServer) && (BooleanUtils.isTrue(storageServer.getEnable()))) {
				stopStorageServer();
			}
			DataServer dataServer = Config.currentNode().getData();
			if ((null != dataServer) && (BooleanUtils.isTrue(dataServer.getEnable()))) {
				stopDataServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
