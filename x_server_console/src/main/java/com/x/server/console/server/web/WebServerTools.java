package com.x.server.console.server.web;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.DefaultCharset;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.WebServer;
import com.x.base.core.utils.Host;
import com.x.server.console.server.JettySeverTools;

public class WebServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(WebServerTools.class);

	public static Server start(WebServer webServer) throws Exception {

		/**
		 * 更新x_desktop的center指向
		 */
		updateCenterConfigJson();

		File configDir = new File(Config.base(), "config");

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(500);
		Server server = new Server(threadPool);
		if (webServer.getSslEnable()) {
			addHttpsConnector(configDir, server, webServer.getPort());
		} else {
			addHttpConnector(server, webServer.getPort());
		}

		HandlerList handlers = new HandlerList();
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		resourceHandler.setBaseResource(Resource.newResource(new File(Config.base(), "servers/webServer")));
		handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });

		GzipHandler gzip = new GzipHandler();
		gzip.setHandler(handlers);
		server.setHandler(gzip);

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		server.start();
		logger.info("web server start completed on port:{}.", webServer.getPort());
		return server;
	}

	private static void updateCenterConfigJson() throws Exception {
		File file = new File(Config.base(), "servers/webServer/x_desktop/res/config/config.json");
		if ((!file.exists()) || file.isDirectory()) {
			throw new Exception("can not find config.json for x_desktop.");
		}
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Gson gson = XGsonBuilder.instance();
		Map<String, Object> map = gson.fromJson(FileUtils.readFileToString(file, DefaultCharset.charset), type);
		Map<String, String> center = new HashMap<String, String>();
		String host = Config.nodes().primaryCenterNode();
		if (Host.isRollback(host)) {
			host = "";
		}
		center.put("host", host);
		center.put("port", Config.centerServer().getPort().toString());
		map.put("center", center);
		FileUtils.writeStringToFile(file, gson.toJson(map), DefaultCharset.charset);
	}
}