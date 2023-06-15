package com.x.server.console.server;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.ftpserver.FtpServer;
import org.eclipse.jetty.server.Server;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.application.ApplicationServerTools;
import com.x.server.console.server.center.CenterServerTools;
import com.x.server.console.server.data.DataServerTools;
import com.x.server.console.server.data.DataTcpWebServer;
import com.x.server.console.server.init.InitServerTools;
import com.x.server.console.server.storage.StorageServerTools;
import com.x.server.console.server.web.WebServerTools;

public class Servers {

	private static final Logger LOGGER = LoggerFactory.getLogger(Servers.class);

	private Servers() {

	}

	private static Server initServer;
	private static Server centerServer;
	private static Server webServer;
	private static Server applicationServer;
	private static FtpServer storageServer;
	private static DataTcpWebServer dataServer;

	public static Server getCenterServer() {
		return centerServer;
	}

	public static Server getApplicationServer() {
		return applicationServer;
	}

	public static Server getWebServer() {
		return webServer;
	}

	public static boolean initServerIsStarted() {
		if (null == initServer) {
			return false;
		}
		return initServer.isStarted();
	}

	public static boolean webServerIsStarted() {
		if (null == webServer) {
			return false;
		}
		return webServer.isStarted();
	}

	public static boolean applicationServerIsStarted() {
		if (null == applicationServer) {
			return false;
		}
		return applicationServer.isStarted();
	}

	public static boolean centerServerIsStarted() {
		if (null == centerServer) {
			return false;
		}
		return centerServer.isStarted();
	}

	public static boolean initServerIsRunning() {
		if (null == initServer) {
			return false;
		}
		return initServer.isRunning();
	}

	public static boolean webServerIsRunning() {
		if (null == webServer) {
			return false;
		}
		return webServer.isRunning();
	}

	public static boolean applicationServerIsRunning() {
		if (null == applicationServer) {
			return false;
		}
		return applicationServer.isRunning();
	}

	public static boolean centerServerIsRunning() {
		if (null == centerServer) {
			return false;
		}
		return centerServer.isRunning();
	}

	public static boolean storageServerIsRunning() {
		if (null == storageServer) {
			return false;
		}
		return (!storageServer.isStopped());
	}

	public static boolean dataServerIsRunning() {
		if (null == dataServer) {
			return false;
		}
		return (dataServer.isRunning());
	}

	public static void startInitServer() throws Exception {
		if (initServerIsRunning()) {
			LOGGER.info("init server is running.");
		} else {
			WebServer server = Config.currentNode().getWeb();
			if (null == server) {
				LOGGER.info("web server is not configured.");
			} else if (!BooleanUtils.isTrue(server.getEnable())) {
				LOGGER.info("web server is not enable.");
			} else {
				initServer = InitServerTools.start();
			}
		}
	}

	public static void stopInitServer() throws Exception {
		if (!initServerIsRunning()) {
			LOGGER.info("init server is not running.");
		} else {
			initServer.stop();
		}
	}

	public static void startWebServer() throws Exception {
		if (webServerIsRunning()) {
			LOGGER.info("web server is running.");
		} else {
			WebServer server = Config.currentNode().getWeb();
			if (null == server) {
				LOGGER.info("web server is not configured.");
			} else if (!BooleanUtils.isTrue(server.getEnable())) {
				LOGGER.info("web server is not enable.");
			} else {
				webServer = WebServerTools.start(server);
			}
		}
	}

	public static void stopWebServer() throws Exception {
		if (!webServerIsRunning()) {
			LOGGER.info("web server is not running.");
		} else {
			webServer.stop();
		}
	}

	public static void startApplicationServer() throws Exception {
		if (applicationServerIsRunning()) {
			LOGGER.info("application server is running.");
		} else {
			ApplicationServer server = Config.currentNode().getApplication();
			if (null == server) {
				LOGGER.info("application server is not configured.");
			} else if (!BooleanUtils.isTrue(server.getEnable())) {
				LOGGER.info("application server is not enable.");
			} else {
				applicationServer = ApplicationServerTools.start(server);
			}
		}
	}

	public static void stopApplicationServer() throws Exception {
		if (!applicationServerIsRunning()) {
			LOGGER.info("application server is not running.");
		} else {
			applicationServer.stop();
		}
	}

	public static void startCenterServer() throws Exception {
		if (centerServerIsRunning()) {
			LOGGER.info("center server is running.");
		} else {
			CenterServer server = Config.currentNode().getCenter();
			if (null == server) {
				LOGGER.info("center server is not configured.");
			} else if (!BooleanUtils.isTrue(server.getEnable())) {
				LOGGER.info("center server is not enable.");
			} else {
				centerServer = CenterServerTools.start(server);
			}
		}

	}

	public static void stopCenterServer() throws Exception {
		if (!centerServerIsRunning()) {
			LOGGER.info("center server is not running.");
		} else {
			centerServer.stop();
		}
	}

	public static void startStorageServer() throws Exception {
		if (storageServerIsRunning()) {
			LOGGER.info("storage server is running.");
		} else {
			StorageServer server = Config.currentNode().getStorage();
			if (null == server) {
				LOGGER.info("storagae server is not configured.");
			} else if (!BooleanUtils.isTrue(server.getEnable())) {
				LOGGER.info("storagae server is not enable.");
			} else {
				storageServer = StorageServerTools.start(server);
			}
		}
	}

	public static void stopStorageServer() {
		if (!storageServerIsRunning()) {
			LOGGER.info("storage server is not running.");
		} else {
			storageServer.stop();
		}
	}

	public static void startDataServer() throws Exception {
		if (dataServerIsRunning()) {
			LOGGER.info("data server is running.");
		} else {
			DataServer server = Config.currentNode().getData();
			if (null == server) {
				LOGGER.info("data server is not configured.");
			} else if (!BooleanUtils.isTrue(server.getEnable())) {
				LOGGER.info("data server is not enable.");
			} else {
				dataServer = DataServerTools.start(server);
			}
		}
	}

	public static void stopDataServer() {
		if (!dataServerIsRunning()) {
			LOGGER.info("data server is not running.");
		} else {
			dataServer.stop();
		}
	}

}
