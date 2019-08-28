package com.x.server.console.server.center;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.x.base.core.project.x_program_center;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.server.console.server.JettySeverTools;

public class CenterServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(CenterServerTools.class);

	private static int CENTERSERVER_THREAD_POOL_SIZE_MIN = 5;
	private static int CENTERSERVER_THREAD_POOL_SIZE_MAX = 100;

	public static Server start(CenterServer centerServer) throws Exception {

		cleanWorkDirectory();

		HandlerList handlers = new HandlerList();

		File war = new File(Config.dir_store(), x_program_center.class.getSimpleName() + ".war");
		File dir = new File(Config.dir_servers_centerServer_work(true), x_program_center.class.getSimpleName());
		if (war.exists()) {
			modified(war, dir);
			QuickStartWebApp webApp = new QuickStartWebApp();
			webApp.setAutoPreconfigure(false);
			webApp.setDisplayName(x_program_center.class.getSimpleName());
			webApp.setContextPath("/" + x_program_center.class.getSimpleName());
			webApp.setResourceBase(dir.getAbsolutePath());
			webApp.setDescriptor(new File(dir, "WEB-INF/web.xml").getAbsolutePath());
			webApp.setExtraClasspath(calculateExtraClassPath(x_program_center.class));
			webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
			webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled", "true");
			webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
			handlers.addHandler(webApp);
		} else {
			throw new Exception("centerServer war not exist.");
		}

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(CENTERSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(CENTERSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("maxFormContentSize", 1024 * 1024 * 1024 * 10);

		if (centerServer.getSslEnable()) {
			addHttpsConnector(server, centerServer.getPort());
		} else {
			addHttpConnector(server, centerServer.getPort());
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

	private static void modified(File war, File dir) throws Exception {
		File lastModified = new File(dir, "WEB-INF/lastModified");
		if ((!lastModified.exists()) || lastModified.isDirectory() || (war.lastModified() != NumberUtils
				.toLong(FileUtils.readFileToString(lastModified, DefaultCharset.charset_utf_8), 0))) {
			if (dir.exists()) {
				FileUtils.forceDelete(dir);
			}
			JarTools.unjar(war, "", dir, true);
			FileUtils.writeStringToFile(lastModified, war.lastModified() + "", DefaultCharset.charset_utf_8, false);
		}
	}

}