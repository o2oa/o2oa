package com.x.server.console.server;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.ftpserver.FtpServer;
import org.eclipse.jetty.server.Server;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.server.console.server.application.ApplicationServerTools;
import com.x.server.console.server.center.CenterServerTools;
import com.x.server.console.server.data.DataServerTools;
import com.x.server.console.server.data.DataTcpWebServer;
import com.x.server.console.server.storage.StorageServerTools;
import com.x.server.console.server.web.WebServerTools;

public class Servers {

	public static Server centerServer;
	public static Server webServer;
	public static Server applicationServer;
	public static FtpServer storageServer;
	public static DataTcpWebServer dataServer;

	public static Boolean webServerIsRunning() {
		if (null == webServer) {
			return false;
		}
		return webServer.isRunning();
	}

	public static Boolean applicationServerIsRunning() {
		if (null == applicationServer) {
			return false;
		}
		return applicationServer.isRunning();
	}

	public static Boolean centerServerIsRunning() {
		if (null == centerServer) {
			return false;
		}
		return centerServer.isRunning();
	}

	public static Boolean storageServerIsRunning() {
		if (null == storageServer) {
			return false;
		}
		return (!storageServer.isStopped());
	}

	public static Boolean dataServerIsRunning() {
		if (null == dataServer) {
			return false;
		}
		return (dataServer.isRunning());
	}

	public static void startWebServer() throws Exception {
		if (webServerIsRunning()) {
			throw new Exception("web server is running.");
		} else {
			WebServer server = Config.currentNode().getWeb();
			if (null == server) {
				throw new Exception("not config webServer.");
			}
			if (!BooleanUtils.isTrue(server.getEnable())) {
				throw new Exception("webServer not enable.");
			}
			webServer = WebServerTools.start(server);
		}
	}

	public static void stopWebServer() throws Exception {
		if (!webServerIsRunning()) {
			throw new Exception("web server is not running.");
		}
		webServer.stop();
	}

	public static void startApplicationServer() throws Exception {
		if (applicationServerIsRunning()) {
			throw new Exception("application server is running.");
		} else {
			ApplicationServer server = Config.currentNode().getApplication();
			if (null == server) {
				throw new Exception("not config applicationServer.");
			}
			if (!BooleanUtils.isTrue(server.getEnable())) {
				throw new Exception("applicationServer not enable.");
			}
			applicationServer = ApplicationServerTools.start(server);
		}
	}

	public static void stopApplicationServer() throws Exception {
		if (!applicationServerIsRunning()) {
			throw new Exception("application server is not running.");
		}
		applicationServer.stop();
	}

	public static void startCenterServer() throws Exception {
		if (centerServerIsRunning()) {
			throw new Exception("center server is running.");
		} else {
			com.x.base.core.project.config.CenterServer config = Config.nodes().centerServers().get(Config.node());
			if (BooleanUtils.isTrue(config.getEnable())) {
				centerServer = CenterServerTools.start(config);
			}
		}
	}

	public static void stopCenterServer() throws Exception {
		if (!centerServerIsRunning()) {
			throw new Exception("center server is not running.");
		}
		centerServer.stop();
	}

	public static void startStorageServer() throws Exception {
		if (storageServerIsRunning()) {
			throw new Exception("storage server is running.");
		} else {
			StorageServer server = Config.currentNode().getStorage();
			if (null == server) {
				throw new Exception("not config storagaeServer.");
			}
			if (!BooleanUtils.isTrue(server.getEnable())) {
				throw new Exception("storagaeServer not enable.");
			}
			storageServer = StorageServerTools.start(server);
		}
	}

	public static void stopStorageServer() throws Exception {
		if (!storageServerIsRunning()) {
			throw new Exception("storage server is not running.");
		}
		storageServer.stop();
	}

	public static void startDataServer() throws Exception {
		if (dataServerIsRunning()) {
			throw new Exception("data server is running.");
		} else {
			DataServer server = Config.currentNode().getData();
			if (null == server) {
				throw new Exception("not config dataServer.");
			}
			if (!BooleanUtils.isTrue(server.getEnable())) {
				throw new Exception("dataServer not enable.");
			}
			dataServer = DataServerTools.start(server);
		}
	}

	public static void stopDataServer() throws Exception {
		if (!dataServerIsRunning()) {
			throw new Exception("data server is not running.");
		} else {
		}
		dataServer.stop();
	}

}
