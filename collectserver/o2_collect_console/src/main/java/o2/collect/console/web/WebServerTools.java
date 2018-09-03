package o2.collect.console.web;

import java.io.File;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import o2.base.core.project.config.Config;

public class WebServerTools {

	private static Logger logger = LoggerFactory.getLogger(WebServerTools.class);

	public static void start() throws Exception {

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(500);
		Server server = new Server(threadPool);
		HttpConfiguration httpConfiguration = new HttpConfiguration();
		httpConfiguration.setOutputBufferSize(32768);
		httpConfiguration.setRequestHeaderSize(8192);
		httpConfiguration.setResponseHeaderSize(8192);
		httpConfiguration.setSendServerVersion(true);
		httpConfiguration.setSendDateHeader(false);
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
		http.setIdleTimeout(30000);
		http.setPort(Config.webServer().getPort());
		server.addConnector(http);

		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		DefaultHandler defaultHandler = new DefaultHandler();
		handlers.setHandlers(new Handler[] { contexts, defaultHandler });
		server.setHandler(handlers);

		DeploymentManager deployer = new DeploymentManager();
		deployer.setContexts(contexts);

		WebAppProvider webAppProvider = new WebAppProvider();
		webAppProvider.setMonitoredDirName(new File(Config.base(), "servers/webServer/webapps").getAbsolutePath());
		webAppProvider.setDefaultsDescriptor(new File(Config.base(), "config/webdefault.xml").getAbsolutePath());
		webAppProvider.setExtractWars(false);
		webAppProvider.setConfigurationManager(new PropertiesConfigurationManager());
		deployer.addAppProvider(webAppProvider);
		server.addBean(deployer);

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		server.start();
		logger.info("web server start completed.");
	}

}