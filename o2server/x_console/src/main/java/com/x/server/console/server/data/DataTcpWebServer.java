package com.x.server.console.server.data;

import org.h2.tools.Server;

public class DataTcpWebServer {

	private Server tcpServer;
	private Server webServer;

	public DataTcpWebServer(Server tcpServer, Server webServer) {
		this.tcpServer = tcpServer;
		this.webServer = webServer;
	}

	public Boolean isRunning() {
		if (tcpServer == null) {
			return false;
		}
		return tcpServer.isRunning(false);
	}

	public Server getTcpServer() {
		return tcpServer;
	}

	public void setTcpServer(Server tcpServer) {
		this.tcpServer = tcpServer;
	}

	public Server getWebServer() {
		return webServer;
	}

	public void setWebServer(Server webServer) {
		this.webServer = webServer;
	}

	public void stop() {
		this.tcpServer.stop();
		if ((null != this.webServer) && (this.webServer.isRunning(false))) {
			this.webServer.stop();
		}
	}

}
