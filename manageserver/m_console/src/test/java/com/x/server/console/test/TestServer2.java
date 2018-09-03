package com.x.server.console.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer2 {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		WebAppContext context = new WebAppContext(server, "d:/testapp", "/aaaa");
		server.start();
		//server.join();
	}
}