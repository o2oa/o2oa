package com.x.server.console.server.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.Packages;
import com.x.base.core.project.ServiceA;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.server.JettySeverTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class ApplicationServerTools extends JettySeverTools {

	private static String project_class_prefix = "com.x.base.core.project.";

	private static Logger logger = LoggerFactory.getLogger(ApplicationServerTools.class);

	private static int APPLICATIONSERVER_THREAD_POOL_SIZE_MIN = 10;
	private static int APPLICATIONSERVER_THREAD_POOL_SIZE_MAX = 50;

	public static Server start(ApplicationServer applicationServer) throws Exception {
		File commonsDir = new File(Config.base(), "commons");
		File webappsDir = new File(Config.base(), "servers/applicationServer/webapps");
		File workDir = new File(Config.base(), "servers/applicationServer/work");
		File extDir = new File(Config.base(), "commons/ext");
		File storeDir = new File(Config.base(), "store");
		File jarsDir = new File(Config.base(), "store/jars");

		if (BooleanUtils.isTrue(applicationServer.getRedeploy())) {
			cleanDirectory(workDir);
			cleanDirectory(webappsDir);
			List<Class<?>> classes = calculateProjectToDepoly();
			for (Class<?> clz : classes) {
				/** 检查store目录是否存在war文件 */
				File war = new File(storeDir, clz.getSimpleName() + ".war");
				if (war.exists()) {
					/* 创建空tempDirectory目录 */
					FileUtils.forceMkdir(new File(workDir, clz.getSimpleName()));
					createDeployDescriptor(clz, webappsDir, workDir, storeDir, extDir, jarsDir);
				}
			}
		}
		// ArrayBlockingQueue<Runnable> queue = new
		// ArrayBlockingQueue<Runnable>(1000);
		// QueuedThreadPool threadPool = new QueuedThreadPool(10, 50, 60000,
		// queue);
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);

		ClassList classlist = ClassList.setServerDefault(server);
		classlist.addAfter(FragmentConfiguration.class.getName(), EnvConfiguration.class.getName(),
				PlusConfiguration.class.getName());
		classlist.addBefore(JettyWebXmlConfiguration.class.getName(), AnnotationConfiguration.class.getName());

		if (applicationServer.getSslEnable()) {
			addHttpsConnector(server, applicationServer.getPort());
		} else {
			addHttpConnector(server, applicationServer.getPort());
		}

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		DeploymentManager deployer = new DeploymentManager();
		deployer.setContextAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern", "nothing.jar");
		deployer.setContexts(contexts);

		WebAppProvider webAppProvider = new WebAppProvider();
		webAppProvider.setMonitoredDirName(webappsDir.getAbsolutePath());
		webAppProvider.setDefaultsDescriptor(new File(commonsDir, "webdefault_a.xml").getAbsolutePath());
		webAppProvider.setScanInterval(applicationServer.getScanInterval());
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
		logger.print("application server start completed on port:{}.", applicationServer.getPort());
		return server;
	}

	private static List<Class<?>> calculateProjectToDepoly() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> list = new ArrayList<>();
		list.addAll(scanResult.getNamesOfSubclassesOf(AssembleA.class));
		list.addAll(scanResult.getNamesOfSubclassesOf(ServiceA.class));
		List<String> includes = new ArrayList<>();
		List<String> excludes = new ArrayList<>();
		if (ListTools.isNotEmpty(Config.currentNode().getApplication().getIncludes())) {
			for (String str : Config.currentNode().getApplication().getIncludes()) {
				if (!StringUtils.startsWith(str, project_class_prefix)) {
					str = project_class_prefix + str;
				}
				includes.add(str);
			}
		}
		if (ListTools.isNotEmpty(Config.currentNode().getApplication().getExcludes())) {
			for (String str : Config.currentNode().getApplication().getExcludes()) {
				if (!StringUtils.startsWith(str, project_class_prefix)) {
					str = project_class_prefix + str;
				}
				excludes.add(str);
			}
		}
		list = ListTools.includesExcludesWildcard(list, includes, excludes);
		List<Class<?>> clzs = new ArrayList<>();
		for (String o : list) {
			clzs.add(Class.forName(o));
		}
		return clzs;
	}
}