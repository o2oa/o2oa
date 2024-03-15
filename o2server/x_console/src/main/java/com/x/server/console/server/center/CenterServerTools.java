package com.x.server.console.server.center;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.ApiAccessFilter;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.JarTools;
import com.x.base.core.project.tools.PathTools;
import com.x.base.core.project.x_program_center;
import com.x.server.console.server.JettySeverTools;
import com.x.server.console.server.ServerRequestLog;
import com.x.server.console.server.ServerRequestLogBody;
import com.x.server.console.server.Servers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.AsyncRequestLogWriter;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Objects;
import java.util.TimeZone;

public class CenterServerTools extends JettySeverTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(CenterServerTools.class);

	public static Server start() throws Exception {

		cleanWorkDirectory();

		Path war = Paths.get(Config.dir_store().toString(), x_program_center.class.getSimpleName() + PathTools.DOT_WAR);
		Path dir = Paths.get(Config.dir_servers_centerServer_work(true).toString(),
				x_program_center.class.getSimpleName());

		modified(war, dir);

		CenterServer centerServer = Config.currentNode().getCenter();

		if ((null == centerServer) || BooleanUtils.isNotTrue(centerServer.getEnable())
				|| Objects.equals(Config.currentNode().getApplication().getPort(), centerServer.getPort())) {
			return null;
		} else {
			return startStandalone(centerServer);
		}

	}

	public static Server startInApplication(CenterServer centerServer) throws Exception {
		WebAppContext webContext = webContext(centerServer);
		GzipHandler gzipHandler = (GzipHandler) Servers.getApplicationServer().getHandler();
		HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
		hanlderList.addHandler(webContext);
		webContext.start();
		LOGGER.print("****************************************");
		LOGGER.print("* center server is started in the application server.");
		LOGGER.print("* port: {}.", Config.currentNode().getApplication().getPort());
		LOGGER.print("****************************************");
		return Servers.getApplicationServer();
	}

	private static Server startStandalone(CenterServer centerServer) throws Exception, IOException {
		HandlerList handlers = new HandlerList();

		QuickStartWebApp webApp = webContext(centerServer);
		handlers.addHandler(webApp);

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("CenterServerQueuedThreadPool");
		threadPool.setMinThreads(THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", MAX_FORM_CONTENT_SIZE);

		if (BooleanUtils.isTrue(centerServer.getSslEnable())) {
			addHttpsConnector(server, centerServer.getPort(), true);
		} else {
			addHttpConnector(server, centerServer.getPort(), true);
		}

		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handlers);
		server.setHandler(gzipHandler);

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		if (BooleanUtils.isTrue(Config.general().getRequestLogEnable())
				|| BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			server.setRequestLog(requestLog(centerServer));
		}

		server.start();

		LOGGER.print("****************************************");
		LOGGER.print("* center server start completed.");
		LOGGER.print("* port: {}.", centerServer.getPort());
		LOGGER.print("****************************************");
		return server;
	}

	public static QuickStartWebApp webContext(CenterServer centerServer) throws Exception {
		Path dir = Paths.get(Config.dir_servers_centerServer_work(true).toString(),
				x_program_center.class.getSimpleName());
		QuickStartWebApp webApp = new QuickStartWebApp();
		webApp.setAutoPreconfigure(false);
		webApp.setDisplayName(x_program_center.class.getSimpleName());
		webApp.setContextPath("/" + x_program_center.class.getSimpleName());
		webApp.setResourceBase(dir.toAbsolutePath().toString());
		webApp.setDescriptor(dir.resolve(Paths.get(PathTools.WEB_INF_WEB_XML)).toString());
		// 加载 ext 目录中的 jar包
		Path ext = dir.resolve("WEB-INF").resolve("ext");
		if (Files.exists(ext)) {
			webApp.setExtraClasspath(calculateExtraClassPath(x_program_center.class, ext));
		} else {
			webApp.setExtraClasspath(calculateExtraClassPath(x_program_center.class));
		}
		webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
				BooleanUtils.toStringTrueFalse(false));
		webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled", BooleanUtils.toStringTrueFalse(true));
		webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
				BooleanUtils.toStringTrueFalse(false));
		setStat(centerServer, webApp);
		setExposeJest(webApp);
		return webApp;
	}

	private static void setStat(CenterServer centerServer, QuickStartWebApp webApp) throws Exception {
		if (BooleanUtils.isTrue(Config.general().getStatEnable())) {
			FilterHolder statFilterHolder = new FilterHolder(new WebStatFilter());
			statFilterHolder.setInitParameter("exclusions", Config.general().getStatExclusions());
			webApp.addFilter(statFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
			ServletHolder statServletHolder = new ServletHolder(StatViewServlet.class);
			statServletHolder.setInitParameter("sessionStatEnable", "false");
			webApp.addServlet(statServletHolder, "/druid/*");
		}
	}

	private static void setExposeJest( QuickStartWebApp webApp) throws Exception {
		FilterHolder denialOfServiceFilterHolder = new FilterHolder(new ApiAccessFilter());
		webApp.addFilter(denialOfServiceFilterHolder, "/jest/*", EnumSet.of(DispatcherType.REQUEST));
		webApp.addFilter(denialOfServiceFilterHolder, "/describe/sources/*", EnumSet.of(DispatcherType.REQUEST));
	}

	private static RequestLog requestLog(CenterServer centerServer) throws Exception {
		AsyncRequestLogWriter asyncRequestLogWriter = new AsyncRequestLogWriter();
		asyncRequestLogWriter.setTimeZone(TimeZone.getDefault().getID());
		asyncRequestLogWriter.setAppend(true);
		asyncRequestLogWriter.setRetainDays(Config.general().getRequestLogRetainDays());
		asyncRequestLogWriter.setFilename(
				Config.dir_logs().toString() + File.separator + "center.request.yyyy_MM_dd." + Config.node() + ".log");
		asyncRequestLogWriter.setFilenameDateFormat("yyyyMMdd");
		if (BooleanUtils.isTrue(Config.general().getRequestLogBodyEnable())
				|| BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			return new ServerRequestLog(asyncRequestLogWriter, LOG_FORMAT);
		} else {
			return new ServerRequestLogBody(asyncRequestLogWriter, LOG_FORMAT);
		}
	}

	private static void cleanWorkDirectory() throws Exception {
		for (String str : Config.dir_servers_centerServer_work(true).list()) {
			if (!StringUtils.equals(str, x_program_center.class.getSimpleName())) {
				FileUtils.forceDelete(new File(Config.dir_servers_centerServer_work(), str));
			}
		}
	}

	private static void modified(Path war, Path dir) throws Exception {
		Path lastModified = Paths.get(dir.toString(), PathTools.WEB_INF_LASTMODIFIED);
		if ((!Files.exists(lastModified)) || Files.isDirectory(lastModified)
				|| (Files.getLastModifiedTime(war).toMillis() != NumberUtils
						.toLong(FileUtils.readFileToString(lastModified.toFile(), DefaultCharset.charset_utf_8), 0))) {
			LOGGER.info("deploy war:{}.", war.getFileName().toAbsolutePath());
			if (Files.exists(dir)) {
				PathUtils.cleanDirectory(dir);
			}
			JarTools.unjar(war, "", dir, true);
			if (!Files.exists(lastModified)) {
				Files.createDirectories(lastModified.getParent());
				Files.createFile(lastModified);
			}
			FileUtils.writeStringToFile(lastModified.toFile(), Files.getLastModifiedTime(war).toMillis() + "",
					DefaultCharset.charset_utf_8, false);
		}
		File commonLang = new File(Config.DIR_COMMONS_LANGUAGE);
		if (commonLang.exists() && commonLang.isDirectory()) {
			File languageDir = new File(dir.toString(), PathTools.WEB_INF_CLASSES_LANGUAGE);
			FileTools.forceMkdir(languageDir);
			File[] files = commonLang.listFiles();
			for (File file : files) {
				if (!file.isDirectory()) {
					File languageFile = new File(languageDir, file.getName());
					FileUtils.copyFile(file, languageFile);
				}
			}
		}
	}
}
