package com.x.server.console.server.web;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.DefaultCharset;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.Token;
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
		/** 创建index.html */
		createIndexPage();
		File configDir = new File(Config.base(), "config");

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(500);
		Server server = new Server(threadPool);
		if (webServer.getSslEnable()) {
			addHttpsConnector(configDir, server, webServer.getPort());
		} else {
			addHttpConnector(server, webServer.getPort());
		}
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setBaseResource(Resource.newResource(new File(Config.base(), "servers/webServer")));
		// context.setResourceBase(".");
		context.setParentLoaderPriority(true);
		context.setExtractWAR(false);
		context.setDefaultsDescriptor(new File(Config.base(), "commons/webdefault.xml").getAbsolutePath());
		// context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
		// "false");
		context.setWelcomeFiles(new String[] { "index.html" });
		context.setGzipHandler(new GzipHandler());
		context.setParentLoaderPriority(true);
		server.setHandler(context);
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);
		server.start();
		// ResourceHandler resourceHandler = new ResourceHandler();
		// resourceHandler.setDirectoriesListed(false);
		// resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		// resourceHandler.setBaseResource(Resource.newResource(new
		// File(Config.base(), "servers/webServer")));
		// Resource.setDefaultUseCaches(false);
		// GzipHandler gzipHandler = new GzipHandler();
		// gzipHandler.setSyncFlush(true);
		// gzipHandler.setHandler(resourceHandler);
		// server.setHandler(resourceHandler);
		// // server.setHandler(gzipHandler);
		// server.setDumpAfterStart(false);
		// server.setDumpBeforeStop(false);
		// server.setStopAtShutdown(true);
		// server.start();
		/* 添加wcss支持mimeType必须在server.start()之后执行 */
		// MimeTypes mimeTypes = new MimeTypes();
		// mimeTypes.addMimeMapping("wcss", "application/json;charset=utf-8");
		context.setMimeTypes(Config.mimeTypes());
		System.out.println("web server start completed on port: " + webServer.getPort() + ".");
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
		List<Map<String, String>> centers = new ArrayList<>();
		Map<String, String> center = new HashMap<String, String>();
		/** 写入center地址 */
		String host = Config.nodes().primaryCenterNode();
		if (Host.isRollback(host)) {
			host = "";
		}
		center.put("host", host);
		center.put("port", Config.centerServer().getPort().toString());
		centers.add(center);
		/** 写入proxy地址 */
		if (StringUtils.isNotEmpty(Config.centerServer().getProxyHost())) {
			Map<String, String> proxyCenter = new HashMap<String, String>();
			proxyCenter.put("host", Config.centerServer().getProxyHost());
			proxyCenter.put("port", Config.centerServer().getProxyPort().toString());
			centers.add(proxyCenter);
		}
		map.put("center", centers);
		/** 写入是否初始化 */
		map.put("initManagerChanged", true);
		map.put("initManagerName", "");
		map.put("initManagerPassword", "");
		if (StringUtils.equals(Config.token().initialManagerInstance().getName(), Token.defaultInitialManager)) {
			if (StringUtils.equals(Config.token().getPassword(), Token.initPassword)) {
				map.put("initManagerChanged", false);
				map.put("initManagerName", Token.defaultInitialManager);
				map.put("initManagerPassword", Token.initPassword);
			}
		}
		/** 写入systemName */
		map.put("systemName",
				StringUtils.isEmpty(Config.collect().getName()) ? "www.o2oa.io" : Config.collect().getName());
		map.put("systemTitle", "企业办公平台");
		FileUtils.writeStringToFile(file, gson.toJson(map), DefaultCharset.charset);

	}

	private static void createIndexPage() throws Exception {
		if (null != Config.nodes().webServers()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<!DOCTYPE html>");
			buffer.append("<html>");
			buffer.append("<head>");
			buffer.append("<meta charset=\"UTF-8\">");
			buffer.append("<title>o2 index</title>");
			buffer.append("	</head>");
			buffer.append("<body>");
			for (Entry<String, WebServer> en : Config.nodes().webServers().entrySet()) {
				WebServer o = en.getValue();
				if (BooleanUtils.isTrue(o.getEnable())) {
					String url = BooleanUtils.isTrue(o.getSslEnable()) ? "https://" : "http://";
					url += en.getKey();
					if (BooleanUtils.isTrue(o.getSslEnable())) {
						if (o.getPort() != 443) {
							url += ":" + o.getPort();
						}
					} else {
						if (o.getPort() != 80) {
							url += ":" + o.getPort();
						}
					}
					buffer.append("<a href=\"" + url + "\">" + url + "</a><br/>");
				}

			}
			buffer.append("</body>");
			buffer.append("</html>");
			File file = new File(Config.base(), "index.html");
			FileUtils.write(file, buffer.toString(), DefaultCharset.name);
		}
	}
}