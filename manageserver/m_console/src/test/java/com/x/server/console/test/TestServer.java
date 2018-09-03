package com.x.server.console.test;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

public class TestServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ResourceHandler resource_handler = new ResourceHandler();

		resource_handler.setDirectoriesListed(true);
		resource_handler.setResourceBase("d:/test");

		GzipHandler gzip = new GzipHandler();
		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
		gzip.setHandler(handlers);
		server.setHandler(gzip);

		server.start();
		server.join();
	}
}