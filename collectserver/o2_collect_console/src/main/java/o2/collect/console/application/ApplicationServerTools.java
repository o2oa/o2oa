package o2.collect.console.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration.ClassList;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.ServiceA;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import o2.base.core.project.Packages;
import o2.base.core.project.config.Config;
import o2.collect.console.server.JettySeverTools;

public class ApplicationServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(ApplicationServerTools.class);

	public static void start() throws Exception {

		File webappsDir = new File(Config.base(), "servers/applicationServer/webapps");
		File workDir = new File(Config.base(), "servers/applicationServer/work");
		File extDir = new File(Config.base(), "commons/ext");
		File storeDir = new File(Config.base(), "store");
		File jarsDir = new File(Config.base(), "store/jars");

		cleanDirectory(webappsDir);
		cleanDirectory(workDir);
		List<Class<?>> classes = calculateProjectToDepoly();
		for (Class<?> clz : classes) {
			createDeployDescriptor(clz, webappsDir, workDir, storeDir, extDir, jarsDir);
		}

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(500);
		Server server = new Server(threadPool);

		ClassList classlist = ClassList.setServerDefault(server);
		classlist.addAfter(FragmentConfiguration.class.getName(), EnvConfiguration.class.getName(),
				PlusConfiguration.class.getName());
		classlist.addBefore(JettyWebXmlConfiguration.class.getName(), AnnotationConfiguration.class.getName());

		addHttpConnector(server, Config.applicationServer().getPort());

		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		DefaultHandler defaultHandler = new DefaultHandler();
		handlers.setHandlers(new Handler[] { contexts, defaultHandler });
		server.setHandler(handlers);

		DeploymentManager deployer = new DeploymentManager();
		deployer.setContexts(contexts);

		WebAppProvider webAppProvider = new WebAppProvider();
		webAppProvider.setMonitoredDirName(webappsDir.getAbsolutePath());
		webAppProvider.setDefaultsDescriptor(new File(Config.base(), "config/webdefault.xml").getAbsolutePath());
		webAppProvider.setScanInterval(10);
		webAppProvider.setExtractWars(true);
		webAppProvider.setConfigurationManager(new PropertiesConfigurationManager());
		deployer.addAppProvider(webAppProvider);
		server.addBean(deployer);

		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		server.start();
		logger.info("application server start completed.");
	}

	private static List<Class<?>> calculateProjectToDepoly() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> names = new ArrayList<>();
		names.addAll(scanResult.getNamesOfSubclassesOf(AssembleA.class));
		names.addAll(scanResult.getNamesOfSubclassesOf(ServiceA.class));
		List<Class<?>> clzs = new ArrayList<>();
		for (String o : names) {
			clzs.add(Class.forName(o));
		}
		return clzs;
	}

}