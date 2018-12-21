package com.x.server.console.server.center;

import java.io.File;

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
import com.x.server.console.server.JettySeverTools;

public class CenterServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(CenterServerTools.class);

	private static int CENTERSERVER_THREAD_POOL_SIZE_MIN = 5;
	private static int CENTERSERVER_THREAD_POOL_SIZE_MAX = 20;

	public static Server start(CenterServer centerServer) throws Exception {
		File commonsDir = new File(Config.base(), "commons");
		File webappsDir = new File(Config.base(), "servers/centerServer/webapps");
		File workDir = new File(Config.base(), "servers/centerServer/work");
		File extDir = new File(Config.base(), "commons/ext");
		File storeDir = new File(Config.base(), "store");
		File jarsDir = new File(Config.base(), "store/jars");

		if (BooleanUtils.isTrue(centerServer.getRedeploy())) {
			cleanDirectory(webappsDir);
			cleanDirectory(workDir);
			createDeployDescriptor(x_program_center.class, webappsDir, workDir, storeDir, extDir, jarsDir);
		}
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(CENTERSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(CENTERSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);

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
		webAppProvider.setMonitoredDirName(webappsDir.getAbsolutePath());
		webAppProvider.setDefaultsDescriptor(new File(commonsDir, "webdefault_c.xml").getAbsolutePath());
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
		logger.print("center server start completed on port:{}.", centerServer.getPort());
		return server;
	}
}