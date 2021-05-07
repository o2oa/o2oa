package com.x.server.console.server.center;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import com.x.base.core.project.tools.FileTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.DenialOfServiceFilter;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.base.core.project.tools.PathTools;
import com.x.server.console.server.JettySeverTools;

public class CenterServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(CenterServerTools.class);

	private static final int CENTERSERVER_THREAD_POOL_SIZE_MIN = 50;
	private static final int CENTERSERVER_THREAD_POOL_SIZE_MAX = 500;

	public static Server start(CenterServer centerServer) throws Exception {

		cleanWorkDirectory();

		HandlerList handlers = new HandlerList();

		Path war = Paths.get(Config.dir_store().toString(), x_program_center.class.getSimpleName() + PathTools.DOT_WAR);
		Path dir = Paths.get(Config.dir_servers_centerServer_work(true).toString(),
				x_program_center.class.getSimpleName());
		if (Files.exists(war)) {
			modified(war, dir);
			QuickStartWebApp webApp = new QuickStartWebApp();
			webApp.setAutoPreconfigure(false);
			webApp.setDisplayName(x_program_center.class.getSimpleName());
			webApp.setContextPath("/" + x_program_center.class.getSimpleName());
			webApp.setResourceBase(dir.toAbsolutePath().toString());
			webApp.setDescriptor(dir.resolve(Paths.get(PathTools.WEB_INF_WEB_XML)).toString());
			webApp.setExtraClasspath(calculateExtraClassPath(x_program_center.class));
			webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
					BooleanUtils.toStringTrueFalse(false));
			webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled", BooleanUtils.toStringTrueFalse(true));
			webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
					BooleanUtils.toStringTrueFalse(false));
			if (BooleanUtils.isTrue(centerServer.getStatEnable())) {
				FilterHolder statFilterHolder = new FilterHolder(new WebStatFilter());
				statFilterHolder.setInitParameter("exclusions", centerServer.getStatExclusions());
				webApp.addFilter(statFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
				ServletHolder statServletHolder = new ServletHolder(StatViewServlet.class);
				statServletHolder.setInitParameter("sessionStatEnable", "false");
				webApp.addServlet(statServletHolder, "/druid/*");
			}
			if (BooleanUtils.isFalse(centerServer.getExposeJest())) {
				FilterHolder denialOfServiceFilterHolder = new FilterHolder(new DenialOfServiceFilter());
				webApp.addFilter(denialOfServiceFilterHolder, "/jest/*", EnumSet.of(DispatcherType.REQUEST));
			}
			handlers.addHandler(webApp);
		} else {
			throw new IOException("centerServer war not exist.");
		}

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(CENTERSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(CENTERSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("maxFormContentSize", centerServer.getMaxFormContent() * 1024 * 1024);

		if (BooleanUtils.isTrue(centerServer.getSslEnable())) {
			addHttpsConnector(server, centerServer.getPort(), centerServer.getPersistentConnectionsEnable());
		} else {
			addHttpConnector(server, centerServer.getPort(), centerServer.getPersistentConnectionsEnable());
		}

		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handlers);
		server.setHandler(gzipHandler);

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		server.start();
		Thread.sleep(1000);
		System.out.println("****************************************");
		System.out.println("* center server start completed.");
		System.out.println("* port: " + centerServer.getPort() + ".");
		System.out.println("****************************************");
		return server;
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
			logger.print("deploy war:{}.", war.getFileName().toAbsolutePath());
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
		if(commonLang.exists() && commonLang.isDirectory()){
			File languageDir = new File(dir.toString(), PathTools.WEB_INF_CLASSES_LANGUAGE);
			FileTools.forceMkdir(languageDir);
			File[] files = commonLang.listFiles();
			for(File file : files){
				if(!file.isDirectory()){
					File languageFile = new File(languageDir, file.getName());
					FileUtils.copyFile(file, languageFile);
				}
			}
		}
	}
}
