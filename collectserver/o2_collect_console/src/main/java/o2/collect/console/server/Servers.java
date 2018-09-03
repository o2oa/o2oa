package o2.collect.console.server;

import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.jetty.server.Server;

import o2.base.core.project.config.ApplicationServer;
import o2.base.core.project.config.Config;
import o2.base.core.project.config.DataServer;
import o2.base.core.project.config.WebServer;
import o2.collect.console.server.application.ApplicationServerTools;
import o2.collect.console.server.data.DataServerTools;
import o2.collect.console.server.data.DataTcpWebServer;
import o2.collect.console.server.web.WebServerTools;

public class Servers {

	public static Server webServer;
	public static Server applicationServer;
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
			WebServer server = Config.webServer();
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
			ApplicationServer server = Config.applicationServer();
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

	public static void startDataServer() throws Exception {
		if (dataServerIsRunning()) {
			throw new Exception("data server is running.");
		} else {
			DataServer server = Config.dataServer();
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