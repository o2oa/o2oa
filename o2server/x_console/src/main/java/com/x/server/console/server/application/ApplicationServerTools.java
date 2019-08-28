package com.x.server.console.server.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.x.base.core.project.x_general_assemble_control;
import com.x.base.core.project.x_organization_assemble_authentication;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.x_organization_assemble_personal;
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

	private static Logger logger = LoggerFactory.getLogger(ApplicationServerTools.class);

	private static int APPLICATIONSERVER_THREAD_POOL_SIZE_MIN = 5;
	private static int APPLICATIONSERVER_THREAD_POOL_SIZE_MAX = 100;

	private static final List<String> OFFICIAL_MODULE_SORTED_TEMPLATE = ListTools.toList(
			x_general_assemble_control.class.getName(), x_organization_assemble_express.class.getName(),
			x_organization_assemble_authentication.class.getName(), x_organization_assemble_control.class.getName(),
			x_organization_assemble_personal.class.getName());

	public static Server start(ApplicationServer applicationServer) throws Exception {

		List<ClassInfo> officialClassInfos = listOfficial();

		List<String> customNames = listCustom();

		cleanWorkDirectory(officialClassInfos, customNames);

		HandlerList handlers = new HandlerList();

		logger.print("start to deploy official module, size:{}.", officialClassInfos.size());

		for (ClassInfo info : officialClassInfos) {
			Class<?> clz = Class.forName(info.getName());
			/* 检查store目录是否存在war文件 */
			File war = new File(Config.dir_store(), info.getSimpleName() + ".war");
			File dir = new File(Config.dir_servers_applicationServer_work(), info.getSimpleName());
			if (war.exists()) {
				modified(war, dir);
				QuickStartWebApp webApp = new QuickStartWebApp();
				webApp.setAutoPreconfigure(false);
				webApp.setDisplayName(clz.getSimpleName());
				webApp.setContextPath("/" + clz.getSimpleName());
				webApp.setResourceBase(dir.getAbsolutePath());
				webApp.setDescriptor(new File(dir, "WEB-INF/web.xml").getAbsolutePath());
				webApp.setExtraClasspath(calculateExtraClassPath(clz));
				webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
				webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled", "true");
				webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
				handlers.addHandler(webApp);
			} else if (dir.exists()) {
				FileUtils.forceDelete(dir);
			}
		}

		logger.print("start to deploy custom module, size:{}.", customNames.size());

		for (String name : customNames) {
			File war = new File(Config.dir_custom(), name + ".war");
			File dir = new File(Config.dir_servers_applicationServer_work(), name);
			if (war.exists()) {
				modified(war, dir);
				String className = contextParamProject(dir);
				URLClassLoader classLoader = new URLClassLoader(
						new URL[] { new File(dir, "WEB-INF/classes").toURI().toURL() });
				Class<?> cls = classLoader.loadClass(className);
				QuickStartWebApp webApp = new QuickStartWebApp();
				webApp.setAutoPreconfigure(false);
				webApp.setDisplayName(name);
				webApp.setContextPath("/" + name);
				webApp.setResourceBase(dir.getAbsolutePath());
				webApp.setDescriptor(dir + "/WEB-INF/web.xml");
				webApp.setExtraClasspath(calculateExtraClassPath(cls));
				webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
				webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled", "true");
				handlers.addHandler(webApp);
			} else if (dir.exists()) {
				FileUtils.forceDelete(dir);
			}
		}

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("maxFormContentSize", 1024 * 1024 * 1024 * 10);

		if (applicationServer.getSslEnable()) {
			addHttpsConnector(server, applicationServer.getPort());
		} else {
			addHttpConnector(server, applicationServer.getPort());
		}

		GzipHandler gzipHandler = new GzipHandler();
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
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
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
				Class<?> clz = Class.forName(info.getName());
				Module module = clz.getAnnotation(Module.class);
				if (Objects.equals(ModuleCategory.OFFICIAL, module.category())) {
					filters.add(info.getName());
				}
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
			os = os.stream().sorted(Comparator.comparing(ClassInfo::getName, (x, y) -> {
				int indx = OFFICIAL_MODULE_SORTED_TEMPLATE.indexOf(x);
				int indy = OFFICIAL_MODULE_SORTED_TEMPLATE.indexOf(y);
				if (indx == indy) {
					return 0;
				} else if (indx == -1) {
					return 1;
				} else if (indy == -1) {
					return -1;
				} else {
					return indx - indy;
				}
			})).collect(Collectors.toList());
			return os;
		}
	}

	private static List<String> listCustom() throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : Config.dir_custom(true).list(new WildcardFileFilter("*.war"))) {
			list.add(FilenameUtils.getBaseName(str));
		}
		list = ListTools.includesExcludesWildcard(list, Config.currentNode().getApplication().getIncludes(),
				Config.currentNode().getApplication().getExcludes());
		return list;
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

	private static void cleanWorkDirectory(List<ClassInfo> officialClassInfos, List<String> customNames)
			throws Exception {
		List<String> names = new ArrayList<>();
		for (ClassInfo o : officialClassInfos) {
			names.add(o.getSimpleName());
		}
		names.addAll(customNames);
		for (String str : Config.dir_servers_applicationServer_work(true).list()) {
			if (!names.contains(str)) {
				FileUtils.forceDelete(new File(Config.dir_servers_applicationServer_work(), str));
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