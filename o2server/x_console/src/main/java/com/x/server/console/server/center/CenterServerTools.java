package com.x.server.console.server.center;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration.ClassList;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;

import com.x.base.core.project.x_program_center;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.server.console.server.JettySeverTools;

public class CenterServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(CenterServerTools.class);

	private static int CENTERSERVER_THREAD_POOL_SIZE_MIN = 5;
	private static int CENTERSERVER_THREAD_POOL_SIZE_MAX = 100;

	protected static final String PATH_WEBAPPS = "servers/centerServer/webapps";
	protected static final String PATH_WORK = "servers/centerServer/work";

	public static Server start(CenterServer centerServer) throws Exception {

		if (BooleanUtils.isTrue(centerServer.getRedeploy())) {
			cleanDirectory(Config.dir_servers_centerServer_webapps());
			cleanDirectory(Config.dir_servers_centerServer_work());
			createDeployDescriptor();
		}
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(CENTERSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(CENTERSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("maxFormContentSize", 1024 * 1024 * 1024 * 10);

		ClassList classlist = ClassList.setServerDefault(server);
		classlist.addAfter(FragmentConfiguration.class.getName(), EnvConfiguration.class.getName(),
				PlusConfiguration.class.getName());
		classlist.addBefore(JettyWebXmlConfiguration.class.getName(), AnnotationConfiguration.class.getName());

		if (centerServer.getSslEnable()) {
			addHttpsConnector(server, centerServer.getPort());
		} else {
			addHttpConnector(server, centerServer.getPort());
		}

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		DeploymentManager deployer = new DeploymentManager();
		deployer.setContextAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern", "nothing.jar");
		deployer.setContexts(contexts);

		WebAppProvider webAppProvider = new WebAppProvider();
		webAppProvider.setMonitoredDirName(Config.dir_servers_centerServer_webapps().getAbsolutePath());
		webAppProvider.setDefaultsDescriptor(new File(Config.dir_commons(), "webdefault_c.xml").getAbsolutePath());
		webAppProvider.setScanInterval(centerServer.getScanInterval());
		webAppProvider.setExtractWars(true);
		webAppProvider.setConfigurationManager(new PropertiesConfigurationManager());
		deployer.addAppProvider(webAppProvider);
		server.addBean(deployer);

		GzipHandler gzipHandler = new GzipHandler();
		DefaultHandler defaultHandler = new DefaultHandler();
		/** 禁止显示Contexts */
		defaultHandler.setShowContexts(false);
		/** 禁止显示icon */
		defaultHandler.setServeIcon(false);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { contexts, defaultHandler });
		gzipHandler.setHandler(handlers);
		server.setHandler(gzipHandler);

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		server.start();
		System.out.println("****************************************");
		System.out.println("* center server start completed.");
		System.out.println("* port: " + centerServer.getPort() + ".");
		System.out.println("****************************************");
		return server;
	}

	protected static void createDeployDescriptor() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if (Config.currentNode().getQuickStartWebApp()) {
			buffer.append("<Configure class=\"org.eclipse.jetty.quickstart.QuickStartWebApp\">");
			buffer.append("<Set name=\"autoPreconfigure\">true</Set>");
		} else {
			buffer.append("<Configure class=\"org.eclipse.jetty.webapp.WebAppContext\">");
		}
		buffer.append("<Set name=\"contextPath\">/" + x_program_center.class.getSimpleName() + "</Set>");
		File war = new File(Config.dir_store(), x_program_center.class.getSimpleName() + ".war");
		buffer.append("<Set name=\"war\">" + war.getAbsolutePath() + "</Set>");
		String extraClasspath = calculateExtraClassPath(x_program_center.class);
		buffer.append("<Set name=\"extraClasspath\">" + extraClasspath + "</Set>");
		String tempDirectory = new File(Config.dir_servers_centerServer_work(), x_program_center.class.getSimpleName())
				.getAbsolutePath();
		buffer.append("<Set name=\"tempDirectory\">" + tempDirectory + "</Set>");
		buffer.append("</Configure>");
		File file = new File(Config.dir_servers_centerServer_webapps(),
				x_program_center.class.getSimpleName() + ".xml");
		FileUtils.write(file, buffer.toString(), DefaultCharset.charset);
	}

}