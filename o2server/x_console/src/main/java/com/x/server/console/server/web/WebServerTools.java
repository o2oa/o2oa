package com.x.server.console.server.web;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.EnumSet;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.servlet.DispatcherType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.eclipse.jetty.server.AsyncRequestLogWriter;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.config.WebServers;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.server.console.server.JettySeverTools;
import com.x.server.console.server.ServerRequestLog;

public class WebServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(WebServerTools.class);

	private static final int WEBSERVER_THREAD_POOL_SIZE_MIN = 20;
	private static final int WEBSERVER_THREAD_POOL_SIZE_MAX = 500;

	public static Server start(WebServer webServer) throws Exception {

		// 更新web服务配置信息
		WebServers.updateWebServerConfigJson();
		// 更新 favicon.ico
		updateFavicon();
		// 创建index.html
		createIndexPage();
		// copyDefaultHtml
		copyDefaultHtml();
		// 覆盖 webServer
		coverToWebServer();

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("WebServerQueuedThreadPool");
		threadPool.setMinThreads(WEBSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(WEBSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		if (BooleanUtils.isTrue(webServer.getSslEnable())) {
			addHttpsConnector(server, webServer.getPort(), webServer.getPersistentConnectionsEnable());
		} else {
			addHttpConnector(server, webServer.getPort(), webServer.getPersistentConnectionsEnable());
		}
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setBaseResource(Resource.newResource(new File(Config.base(), "servers/webServer")));
		context.setParentLoaderPriority(true);
		context.setExtractWAR(false);
		context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "" + webServer.getDirAllowed());
		context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
		if (webServer.getCacheControlMaxAge() > 0) {
			context.setInitParameter("org.eclipse.jetty.servlet.Default.cacheControl",
					"max-age=" + webServer.getCacheControlMaxAge());
		}
		context.setInitParameter("org.eclipse.jetty.servlet.Default.maxCacheSize", "256000000");
		context.setInitParameter("org.eclipse.jetty.servlet.Default.maxCachedFileSize", "200000000");
		context.setWelcomeFiles(new String[] { "default.html", "index.html" });
		context.setGzipHandler(new GzipHandler());
		context.setParentLoaderPriority(true);
		context.getMimeTypes().addMimeMapping("wcss", "application/json");
		/* stat */
		if (BooleanUtils.isTrue(webServer.getStatEnable())) {
			FilterHolder statFilterHolder = new FilterHolder(new WebStatFilter());
			statFilterHolder.setInitParameter("exclusions", webServer.getStatExclusions());
			context.addFilter(statFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
			ServletHolder statServletHolder = new ServletHolder(StatViewServlet.class);
			statServletHolder.setInitParameter("sessionStatEnable", "false");
			context.addServlet(statServletHolder, "/druid/*");
		}
		/* stat end */
		server.setHandler(context);

		if (BooleanUtils.isTrue(webServer.getProxyCenterEnable())) {
			proxyCenter(context, webServer.getProxyTimeOut());
		}

		if (BooleanUtils.isTrue(webServer.getProxyApplicationEnable())) {
			proxyApplication(context, Config.dir_store().toPath(), webServer.getProxyTimeOut());
			proxyApplication(context, Config.dir_custom().toPath(), webServer.getProxyTimeOut());
		}

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		if (BooleanUtils.isTrue(webServer.getRequestLogEnable())) {
			server.setRequestLog(requestLog(webServer));
		}

		context.setMimeTypes(Config.mimeTypes());

		server.start();

		System.out.println("****************************************");
		System.out.println("* web server start completed.");
		System.out.println("* port: " + webServer.getPort() + ".");
		System.out.println("****************************************");
		return server;
	}

	private static RequestLog requestLog(WebServer webServer) throws Exception {
		AsyncRequestLogWriter asyncRequestLogWriter = new AsyncRequestLogWriter();
		asyncRequestLogWriter.setTimeZone(TimeZone.getDefault().getID());
		asyncRequestLogWriter.setAppend(true);
		asyncRequestLogWriter.setRetainDays(webServer.getRequestLogRetainDays());
		asyncRequestLogWriter.setFilename(
				Config.dir_logs().toString() + File.separator + "web.request.yyyy_MM_dd." + Config.node() + ".log");
		asyncRequestLogWriter.setFilenameDateFormat("yyyyMMdd");
		String format = "%{client}a - %u %{yyyy-MM-dd HH:mm:ss.SSS ZZZ|" + DateFormatUtils.format(new Date(), "z")
				+ "}t \"%r\" %s %O %{ms}T";
		return new ServerRequestLog(asyncRequestLogWriter,
				StringUtils.isEmpty(webServer.getRequestLogFormat()) ? format : webServer.getRequestLogFormat());
	}

	private static void proxyCenter(WebAppContext context, final Integer timeout) throws Exception {
		ServletHolder proxyHolder = new ServletHolder(Proxy.class);
		proxyHolder.setInitParameter("port", Config.currentNode().getCenter().getPort() + "");
		proxyHolder.setInitParameter("idleTimeout", "60000");
		proxyHolder.setInitParameter("timeout", timeout + "000");
		context.addServlet(proxyHolder, "/" + x_program_center.class.getSimpleName() + "/*");
	}

	private static void proxyApplication(WebAppContext context, Path path, final Integer timeout) throws Exception {
		try (Stream<Path> stream = Files.list(path)) {
			stream.filter(o -> StringUtils.endsWithIgnoreCase(o.getFileName().toString(), ".war"))
					.map(Path::getFileName).map(Path::toString).map(FilenameUtils::getBaseName)
					.filter(o -> !StringUtils.equals(o, x_program_center.class.getSimpleName())).forEach(o -> {
						try {
							ServletHolder proxyHolder = new ServletHolder(Proxy.class);
							proxyHolder.setInitParameter("port", Config.currentNode().getApplication().getPort() + "");
							proxyHolder.setInitParameter("idleTimeout", "60000");
							proxyHolder.setInitParameter("timeout", timeout + "000");
							context.addServlet(proxyHolder, "/" + o + "/*");
						} catch (Exception e) {
							logger.error(e);
						}
					});
		}
	}

	private static void copyDefaultHtml() throws Exception {
		File file = new File(Config.dir_config(), "default.html");
		if (file.exists() && file.isFile()) {
			FileUtils.copyFile(file, new File(Config.base(), "servers/webServer/default.html"));
		}
	}

	private static void updateFavicon() throws Exception {

		File file = new File(Config.dir_config(), "favicon.ico");

		if (file.exists() && file.isFile()) {
			FileUtils.copyFile(file, new File(Config.dir_servers_webServer(), "favicon.ico"));
		}

	}

	private static void createIndexPage() throws Exception {
		if (null == Config.nodes().webServers()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>").append("<html>").append("<head>").append("<meta charset=\"UTF-8\">")
				.append("<title>o2 index</title>").append("</head>").append("<body>");
		for (Entry<String, WebServer> en : Config.nodes().webServers().entrySet()) {
			createIndexPagePerWebServer(sb, en);
		}
		sb.append("</body>").append("</html>");
		File file = new File(Config.base(), "index.html");
		FileUtils.write(file, sb.toString(), DefaultCharset.name);
	}

	private static void createIndexPagePerWebServer(StringBuilder sb, Entry<String, WebServer> en) {
		WebServer o = en.getValue();
		if (BooleanUtils.isTrue(o.getEnable())) {
			String url = BooleanUtils.isTrue(o.getSslEnable()) ? "https://" : "http://";
			url += en.getKey();
			if (BooleanUtils.isTrue(o.getSslEnable())) {
				url += o.getPort() != 443 ? (":" + o.getPort()) : "";
			} else {
				url += o.getPort() != 80 ? (":" + o.getPort()) : "";
			}
			sb.append("<a href=\"" + url + "\">" + url + "</a><br/>");
		}
	}

	private static void coverToWebServer() throws Exception {
		Path p = Config.path_config_coverToWebServer(true);
		if (Files.exists(p)) {
			FileUtils.copyDirectory(p.toFile(), Config.path_servers_webServer(true).toFile());
		}
	}
}
