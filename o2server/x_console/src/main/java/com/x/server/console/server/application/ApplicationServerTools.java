package com.x.server.console.server.application;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.ApiAccessFilter;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.*;
import com.x.server.console.node.RegistApplicationsEvent;
import com.x.server.console.node.UpdateApplicationsEvent;
import com.x.server.console.server.JettySeverTools;
import com.x.server.console.server.ServerRequestLog;
import com.x.server.console.server.ServerRequestLogBody;
import com.x.server.console.server.center.CenterServerTools;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
import org.w3c.dom.Document;

import javax.servlet.DispatcherType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationServerTools extends JettySeverTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServerTools.class);

	public static Server start() throws Exception {

		ApplicationServer applicationServer = Config.currentNode().getApplication();

		List<ClassInfo> officialClassInfos = listOfficial();

		List<String> customNames = listCustom();

		cleanWorkDirectory(officialClassInfos, customNames);

		HandlerList handlers = new HandlerList();

		Server server = createServer(applicationServer, handlers);

		if (Objects.equals(Config.currentNode().getCenter().getPort(), applicationServer.getPort())) {
			WebAppContext webContext = CenterServerTools.webContext(Config.currentNode().getCenter());
			handlers.addHandler(webContext);
			webContext.start();
			// Servers.centerServer = server;
			LOGGER.print("****************************************");
			LOGGER.print("* center server is started in the application server.");
			LOGGER.print("* port: " + Config.currentNode().getApplication().getPort() + ".");
			LOGGER.print("****************************************");
		}

		LOGGER.info("start to deploy official module: {}, custom module: {}.", officialClassInfos.size(),
				customNames.size());

		deployOfficial(applicationServer, handlers, officialClassInfos);

		deployCustom(applicationServer, handlers, customNames);

		// 将应用首先注册到本地,开机可以直接运行
		new RegistApplicationsLocal().execute(server);
		// 注册本地应用并推送到服务器
		new RegistApplicationsEvent().execute(server);
		new UpdateApplicationsEvent().execute(server);

		LOGGER.print("****************************************");
		LOGGER.print("* application server start completed.");
		LOGGER.print("* port: " + applicationServer.getPort() + ".");
		LOGGER.print("****************************************");
		return server;
	}

	private static Server createServer(ApplicationServer applicationServer, HandlerList handlers) throws Exception {
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("ApplicationServerQueuedThreadPool");
		threadPool.setMinThreads(THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", MAX_FORM_CONTENT_SIZE);
		if (BooleanUtils.isTrue(applicationServer.getSslEnable())) {
			addHttpsConnector(server, applicationServer.getPort(), true);
		} else {
			addHttpConnector(server, applicationServer.getPort(), true);
		}
		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handlers);
		server.setHandler(gzipHandler);
		if (BooleanUtils.isTrue(Config.general().getRequestLogEnable())
				|| BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			server.setRequestLog(requestLog(applicationServer));
		}
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);
		server.start();
		return server;
	}

	private static RequestLog requestLog(ApplicationServer applicationServer) throws Exception {
		AsyncRequestLogWriter asyncRequestLogWriter = new AsyncRequestLogWriter();
		asyncRequestLogWriter.setTimeZone(TimeZone.getDefault().getID());
		asyncRequestLogWriter.setAppend(true);
		asyncRequestLogWriter.setRetainDays(Config.general().getRequestLogRetainDays());
		asyncRequestLogWriter.setFilename(Config.dir_logs().toString() + File.separator
				+ "application.request.yyyy_MM_dd." + Config.node() + ".log");
		asyncRequestLogWriter.setFilenameDateFormat("yyyyMMdd");
		if (BooleanUtils.isTrue(Config.general().getRequestLogBodyEnable())
				|| BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			return new ServerRequestLogBody(asyncRequestLogWriter, LOG_FORMAT);
		} else {
			return new ServerRequestLog(asyncRequestLogWriter, LOG_FORMAT);
		}
	}

	private static void deployCustom(ApplicationServer applicationServer, HandlerList handlers,
			List<String> customNames) {
		customNames.stream().forEach(name -> {
			try {
				Path war = Paths.get(Config.dir_custom().toString(), name + PathTools.DOT_WAR);
				Path dir = Paths.get(Config.dir_servers_applicationServer_work().toString(), name);
				if (Files.exists(war)) {
					modified(war, dir);
					String className = contextParamProject(dir);
					Class<?> cls = ClassLoaderTools.urlClassLoader(null, false, false, false, false,
							Paths.get(dir.toString(), PathTools.WEB_INF_CLASSES)).loadClass(className);
					QuickStartWebApp webApp = new QuickStartWebApp();
					webApp.setAutoPreconfigure(false);
					webApp.setDisplayName(name);
					webApp.setContextPath("/" + name);
					webApp.setResourceBase(dir.toAbsolutePath().toString());
					webApp.setDescriptor(dir.resolve(Paths.get(PathTools.WEB_INF_WEB_XML)).toString());
					Path ext = dir.resolve("WEB-INF").resolve("ext");
					if (Files.exists(ext)) {
						webApp.setExtraClasspath(calculateExtraClassPath(cls, ext));
					} else {
						webApp.setExtraClasspath(calculateExtraClassPath(cls));
					}
					LOGGER.debug("{} extra class path:{}.", name, webApp.getExtraClasspath());
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
							BooleanUtils.toStringTrueFalse(false));
					webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled",
							BooleanUtils.toStringTrueFalse(true));
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
							BooleanUtils.toStringTrueFalse(false));
					setStat(applicationServer, webApp);
					setExposeJest(webApp);
					handlers.addHandler(webApp);
					webApp.start();
				} else if (Files.exists(dir)) {
					PathUtils.cleanDirectory(dir);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private static void setExposeJest(QuickStartWebApp webApp) {
		FilterHolder denialOfServiceFilterHolder = new FilterHolder(new ApiAccessFilter());
		webApp.addFilter(denialOfServiceFilterHolder, "/jest/*", EnumSet.of(DispatcherType.REQUEST));
		webApp.addFilter(denialOfServiceFilterHolder, "/describe/sources/*", EnumSet.of(DispatcherType.REQUEST));
	}

	private static void setStat(ApplicationServer applicationServer, QuickStartWebApp webApp) throws Exception {
		if (BooleanUtils.isTrue(Config.general().getStatEnable())) {
			FilterHolder statFilterHolder = new FilterHolder(new WebStatFilter());
			statFilterHolder.setInitParameter("exclusions", Config.general().getStatExclusions());
			webApp.addFilter(statFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
			ServletHolder statServletHolder = new ServletHolder(StatViewServlet.class);
			statServletHolder.setInitParameter("sessionStatEnable", BooleanUtils.toStringTrueFalse(false));
			webApp.addServlet(statServletHolder, "/druid/*");
		}
	}

	private static void deployOfficial(ApplicationServer applicationServer, HandlerList handlers,
			List<ClassInfo> officialClassInfos) {
		officialClassInfos.parallelStream().forEach(info -> {
			try {
				Class<?> clz = Thread.currentThread().getContextClassLoader().loadClass(info.getName());
				Path war = Paths.get(Config.dir_store().toString(), info.getSimpleName() + PathTools.DOT_WAR);
				Path dir = Paths.get(Config.dir_servers_applicationServer_work().toString(), info.getSimpleName());
				if (Files.exists(war)) {
					modified(war, dir);
					QuickStartWebApp webApp = new QuickStartWebApp();
					webApp.setAutoPreconfigure(false);
					webApp.setDisplayName(clz.getSimpleName());
					webApp.setContextPath("/" + clz.getSimpleName());
					webApp.setResourceBase(dir.toAbsolutePath().toString());
					webApp.setDescriptor(dir.resolve(Paths.get(PathTools.WEB_INF_WEB_XML)).toString());
					Path ext = dir.resolve("WEB-INF").resolve("ext");
					if (Files.exists(ext)) {
						webApp.setExtraClasspath(calculateExtraClassPath(clz, ext));
					} else {
						webApp.setExtraClasspath(calculateExtraClassPath(clz));
					}
					LOGGER.debug("{} extra class path:{}.", clz::getSimpleName, webApp::getExtraClasspath);
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
							BooleanUtils.toStringTrueFalse(false));
					webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled",
							BooleanUtils.toStringTrueFalse(true));
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
							BooleanUtils.toStringTrueFalse(false));
					setStat(applicationServer, webApp);
					setExposeJest(webApp);
					handlers.addHandler(webApp);
					webApp.start();
				} else if (Files.exists(dir)) {
					PathUtils.cleanDirectory(dir);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private static List<ClassInfo> listOfficial() throws Exception {
		try (ScanResult scanResult = new ClassGraph()
				.addClassLoader(
						ClassLoaderTools.urlClassLoader(ClassLoader.getSystemClassLoader(), false, true, false, false))
				.enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			List<String> filters = classInfos.stream().filter(info -> {
				try {
					Module module = Thread.currentThread().getContextClassLoader().loadClass(info.getName())
							.getAnnotation(Module.class);
					return ((Objects.equals(ModuleCategory.OFFICIAL, module.category()))
							&& (Objects.equals(module.type(), ModuleType.ASSEMBLE)
									|| Objects.equals(module.type(), ModuleType.SERVICE)));
				} catch (ClassNotFoundException e) {
					LOGGER.error(e);
				}
				return false;
			}).map(ClassInfo::getName).collect(Collectors.toList());
			final List<String> names = StringTools.includesExcludesWithWildcard(filters,
					Config.currentNode().getApplication().getIncludes(),
					Config.currentNode().getApplication().getExcludes());
			return classInfos.stream().filter(info -> names.contains(info.getName()))
					.sorted(Comparator.comparing(ClassInfo::getName, (x, y) -> {
						int indx = Applications.OFFICIAL_APPLICATIONS.indexOf(x);
						int indy = Applications.OFFICIAL_APPLICATIONS.indexOf(y);
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
		}
	}

	private static List<String> listCustom() throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : Config.dir_custom(true).list(new WildcardFileFilter("*" + PathTools.DOT_WAR))) {
			list.add(FilenameUtils.getBaseName(str));
		}
		list = ListTools.includesExcludesWildcard(list, Config.currentNode().getApplication().getIncludes(),
				Config.currentNode().getApplication().getExcludes());
		return list;
	}

	private static String contextParamProject(Path dir) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(
				new ByteArrayInputStream(Files.readAllBytes(Paths.get(dir.toString(), PathTools.WEB_INF_WEB_XML))));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("web-app/context-param[param-name='project']/param-value");
		String str = expr.evaluate(doc, XPathConstants.STRING).toString();
		return StringUtils.trim(str);
	}

	private static void cleanWorkDirectory(List<ClassInfo> officialClassInfos, List<String> customNames)
			throws Exception {
		List<String> names = new ArrayList<>();
		officialClassInfos.stream().map(ClassInfo::getSimpleName).forEach(names::add);
		names.addAll(customNames);
		for (String str : Config.dir_servers_applicationServer_work(true).list()) {
			if (!names.contains(str)) {
				FileUtils.forceDelete(new File(Config.dir_servers_applicationServer_work(), str));
			}
		}
	}

	private static void modified(Path war, Path dir) throws Exception {
		Path lastModified = Paths.get(dir.toString(), PathTools.WEB_INF_LASTMODIFIED);
		if ((!Files.exists(lastModified)) || Files.isDirectory(lastModified)
				|| (Files.getLastModifiedTime(war).toMillis() != NumberUtils
						.toLong(FileUtils.readFileToString(lastModified.toFile(), DefaultCharset.charset_utf_8), 0))) {
			LOGGER.print("deploy war:{}.", war.getFileName().toAbsolutePath());
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
