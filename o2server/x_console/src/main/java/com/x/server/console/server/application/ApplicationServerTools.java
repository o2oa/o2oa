package com.x.server.console.server.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.server.console.server.JettySeverTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ApplicationServerTools extends JettySeverTools {

	// private static String project_class_prefix = "com.x.base.core.project.";

	private static Logger logger = LoggerFactory.getLogger(ApplicationServerTools.class);

	private static int APPLICATIONSERVER_THREAD_POOL_SIZE_MIN = 5;
	private static int APPLICATIONSERVER_THREAD_POOL_SIZE_MAX = 100;

	protected static String PATH_WEBAPPS = "servers/applicationServer/webapps";
	protected static String PATH_WORK = "servers/applicationServer/work";

	public static Server start(ApplicationServer applicationServer) throws Exception {

		if (BooleanUtils.isTrue(applicationServer.getRedeploy())) {
			cleanDirectory(Config.dir_servers_applicationServer_work());
			cleanDirectory(Config.dir_servers_applicationServer_webapps());
			List<ClassInfo> classInfoList = listOfficial();
			logger.print("start to deploy official module, size:{}.", classInfoList.size());
			for (ClassInfo info : classInfoList) {
				Class<?> clz = Class.forName(info.getName());
				Module module = clz.getAnnotation(Module.class);
				if (Objects.equals(ModuleCategory.OFFICIAL, module.category())) {
					/* 检查store目录是否存在war文件 */
					File war = new File(Config.dir_store(), info.getSimpleName() + ".war");
					if (war.exists()) {
						/* 创建空tempDirectory目录 */
						FileUtils.forceMkdir(
								new File(Config.dir_servers_applicationServer_work(), info.getSimpleName()));
						createOfficialDeployDescriptor(info);
					}
				}
			}
			List<String> customWars = listCustom();
			if (!customWars.isEmpty()) {
				logger.print("start to deploy custom module, size:{}.", customWars.size());
				for (String str : customWars) {
					File war = new File(Config.dir_custom(), str);
					if (war.exists()) {
						/* 创建空tempDirectory目录 */
						// FileUtils.forceMkdir(new File(Config, FilenameUtils.getBaseName(str)));
						customDeployDescriptor(war);
					}
				}
			}
		}
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("maxFormContentSize", 1024 * 1024 * 1024 * 10);

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
		webAppProvider.setMonitoredDirName(Config.dir_servers_applicationServer_webapps().getAbsolutePath());
		webAppProvider.setDefaultsDescriptor(new File(Config.dir_commons(), "webdefault_a.xml").getAbsolutePath());
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
		System.out.println("****************************************");
		System.out.println("* application server start completed.");
		System.out.println("* port: " + applicationServer.getPort() + ".");
		System.out.println("****************************************");
		return server;
	}

	private static List<ClassInfo> listOfficial() throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
			List<ClassInfo> list = new ArrayList<>();
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				Class<?> clz = Class.forName(info.getName());
				Module module = clz.getAnnotation(Module.class);
				if (Objects.equals(module.type(), ModuleType.ASSEMBLE)
						|| Objects.equals(module.type(), ModuleType.SERVICE)) {
					list.add(info);
				}
			}
			List<String> filters = new ArrayList<>();
			for (ClassInfo info : list) {
				filters.add(info.getName());
			}
			filters = StringTools.includesExcludesWithWildcard(filters,
					Config.currentNode().getApplication().getIncludes(),
					Config.currentNode().getApplication().getExcludes());
			List<ClassInfo> os = new ArrayList<>();
			for (ClassInfo info : list) {
				if (filters.contains(info.getName())) {
					os.add(info);
				}
			}

			return os;
		}
	}

	private static List<String> listCustom() throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : Config.dir_custom(true)
				.list(FileFilterUtils.or(new WildcardFileFilter("*.WAR"), new WildcardFileFilter("*.war")))) {
			list.add(str);
		}
		list = ListTools.includesExcludesWildcard(list, Config.currentNode().getApplication().getIncludes(),
				Config.currentNode().getApplication().getExcludes());
		return list;
	}

	private static void customDeployDescriptor(File war) throws Exception {
		File unzip_dir = new File(Config.dir_local_temp(), FilenameUtils.getBaseName(war.getName()));
		FileUtils.forceMkdir(unzip_dir);
		FileUtils.cleanDirectory(unzip_dir);
		JarTools.unjar(FileUtils.readFileToByteArray(war), "WEB-INF", unzip_dir, true);
		URLClassLoader classLoader = new URLClassLoader(
				new URL[] { new File(unzip_dir, "WEB-INF/classes").toURI().toURL() });
		String className = contextParamProject(unzip_dir);
		Class<?> cls = classLoader.loadClass(className);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if (Config.currentNode().getQuickStartWebApp()) {
			buffer.append("<Configure class=\"org.eclipse.jetty.quickstart.QuickStartWebApp\">");
			buffer.append("<Set name=\"autoPreconfigure\">true</Set>");
		} else {
			buffer.append("<Configure class=\"org.eclipse.jetty.webapp.WebAppContext\">");
		}
		buffer.append("<Set name=\"contextPath\">/" + FilenameUtils.getBaseName(war.getName()) + "</Set>");
		buffer.append("<Set name=\"war\">" + war.getAbsolutePath() + "</Set>");
		String extraClasspath = calculateExtraClassPath(cls);
		buffer.append("<Set name=\"extraClasspath\">" + extraClasspath + "</Set>");
		String tempDirectory = new File(Config.dir_servers_applicationServer_work(),
				FilenameUtils.getBaseName(war.getAbsolutePath())).getAbsolutePath();
		buffer.append("<Set name=\"tempDirectory\">" + tempDirectory + "</Set>");
		buffer.append("</Configure>");
		/* classLoader需要关闭 */
		classLoader.close();
		File file = new File(Config.dir_servers_applicationServer_webapps(),
				FilenameUtils.getBaseName(war.getName()) + ".xml");
		FileUtils.write(file, buffer.toString(), DefaultCharset.charset);
	}

	private static String contextParamProject(File dir) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder
				.parse(new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(dir, "WEB-INF/web.xml"))));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("web-app/context-param[param-name='project']/param-value");
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		String str = nodes.item(0).getTextContent();
		return StringUtils.trim(str);
	}
}