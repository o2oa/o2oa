package com.x.server.console.server.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase;
import com.x.base.core.project.tools.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
import org.w3c.dom.Document;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.x_bbs_assemble_control;
import com.x.base.core.project.x_calendar_assemble_control;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_component_assemble_control;
import com.x.base.core.project.x_file_assemble_control;
import com.x.base.core.project.x_general_assemble_control;
import com.x.base.core.project.x_hotpic_assemble_control;
import com.x.base.core.project.x_meeting_assemble_control;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.x_mind_assemble_control;
import com.x.base.core.project.x_organization_assemble_authentication;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.x_organization_assemble_personal;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_portal_assemble_surface;
import com.x.base.core.project.x_processplatform_assemble_bam;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.x_query_assemble_surface;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.DenialOfServiceFilter;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.JettySeverTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ApplicationServerTools extends JettySeverTools {

	private static Logger logger = LoggerFactory.getLogger(ApplicationServerTools.class);

	private static final int APPLICATIONSERVER_THREAD_POOL_SIZE_MIN = 50;
	private static final int APPLICATIONSERVER_THREAD_POOL_SIZE_MAX = 500;

	private static final List<String> OFFICIAL_MODULE_SORTED_TEMPLATE = ListTools.toList(
			x_general_assemble_control.class.getName(), x_organization_assemble_authentication.class.getName(),
			x_organization_assemble_express.class.getName(), x_organization_assemble_control.class.getName(),
			x_organization_assemble_personal.class.getName(), x_component_assemble_control.class.getName(),
			x_message_assemble_communicate.class.getName(), x_calendar_assemble_control.class.getName(),
			x_processplatform_service_processing.class.getName(), x_processplatform_assemble_designer.class.getName(),
			x_processplatform_assemble_surface.class.getName(), x_processplatform_assemble_bam.class.getName(),
			x_cms_assemble_control.class.getName(), x_portal_assemble_designer.class.getName(),
			x_portal_assemble_surface.class.getName(), x_attendance_assemble_control.class.getName(),
			x_bbs_assemble_control.class.getName(), x_file_assemble_control.class.getName(),
			x_meeting_assemble_control.class.getName(), x_mind_assemble_control.class.getName(),
			x_hotpic_assemble_control.class.getName(), x_query_service_processing.class.getName(),
			x_query_assemble_designer.class.getName(), x_query_assemble_surface.class.getName());

	public static Server start(ApplicationServer applicationServer) throws Exception {

		List<ClassInfo> officialClassInfos = listOfficial();

		List<String> customNames = listCustom();

		cleanWorkDirectory(officialClassInfos, customNames);

		HandlerList handlers = new HandlerList();

		logger.print("start to deploy official module: {}, custom module: {}.", officialClassInfos.size(),
				customNames.size());

		deployOfficial(applicationServer, handlers, officialClassInfos);

		deployCustom(applicationServer, handlers, customNames);

		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(APPLICATIONSERVER_THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(applicationServer.getMaxThread());
		Server server = new Server(threadPool);
		server.setAttribute("maxFormContentSize", applicationServer.getMaxFormContent() * 1024 * 1024);

		if (BooleanUtils.isTrue(applicationServer.getSslEnable())) {
			addHttpsConnector(server, applicationServer.getPort(), applicationServer.getPersistentConnectionsEnable());
		} else {
			addHttpConnector(server, applicationServer.getPort(), applicationServer.getPersistentConnectionsEnable());
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

	private static void deployCustom(ApplicationServer applicationServer, HandlerList handlers,
			List<String> customNames) {
		customNames.parallelStream().forEach(name -> {
			try {
				Path war = Paths.get(Config.dir_custom().toString(), name + PathTools.DOT_WAR);
				Path dir = Paths.get(Config.dir_servers_applicationServer_work().toString(), name);
				if (Files.exists(war)) {
					modified(war, dir);
					String className = contextParamProject(dir);
					Class<?> cls = ClassLoaderTools.urlClassLoader(false, false, false, false, false,
							Paths.get(dir.toString(), PathTools.WEB_INF_CLASSES)).loadClass(className);
					QuickStartWebApp webApp = new QuickStartWebApp();
					webApp.setAutoPreconfigure(false);
					webApp.setDisplayName(name);
					webApp.setContextPath("/" + name);
					webApp.setResourceBase(dir.toAbsolutePath().toString());
					webApp.setDescriptor(dir.resolve(Paths.get(PathTools.WEB_INF_WEB_XML)).toString());
					webApp.setExtraClasspath(calculateExtraClassPath(cls));
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
							BooleanUtils.toStringTrueFalse(false));
					webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled",
							BooleanUtils.toStringTrueFalse(true));
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
							BooleanUtils.toStringTrueFalse(false));
					if (BooleanUtils.isTrue(applicationServer.getStatEnable())) {
						FilterHolder statFilterHolder = new FilterHolder(new WebStatFilter());
						statFilterHolder.setInitParameter("exclusions", applicationServer.getStatExclusions());
						webApp.addFilter(statFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
						ServletHolder statServletHolder = new ServletHolder(StatViewServlet.class);
						statServletHolder.setInitParameter("sessionStatEnable", BooleanUtils.toStringTrueFalse(false));
						webApp.addServlet(statServletHolder, "/druid/*");
					}
					if (BooleanUtils.isFalse(applicationServer.getExposeJest())) {
						FilterHolder denialOfServiceFilterHolder = new FilterHolder(new DenialOfServiceFilter());
						webApp.addFilter(denialOfServiceFilterHolder, "/jest/*", EnumSet.of(DispatcherType.REQUEST));
					}
					handlers.addHandler(webApp);
				} else if (Files.exists(dir)) {
					PathUtils.cleanDirectory(dir);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private static void deployOfficial(ApplicationServer applicationServer, HandlerList handlers,
			List<ClassInfo> officialClassInfos) {
		officialClassInfos.parallelStream().forEach(info -> {
			try {
				Class<?> clz = Class.forName(info.getName());
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
					webApp.setExtraClasspath(calculateExtraClassPath(clz));
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
							BooleanUtils.toStringTrueFalse(false));
					webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled",
							BooleanUtils.toStringTrueFalse(true));
					webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
							BooleanUtils.toStringTrueFalse(false));
					if (BooleanUtils.isTrue(applicationServer.getStatEnable())) {
						FilterHolder holder = new FilterHolder(new WebStatFilter());
						holder.setInitParameter("exclusions", applicationServer.getStatExclusions());
						webApp.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST));
						webApp.addServlet(StatViewServlet.class, "/druid/*");
					}
					handlers.addHandler(webApp);
				} else if (Files.exists(dir)) {
					PathUtils.cleanDirectory(dir);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private static List<ClassInfo> listOfficial() throws Exception {
		try (ScanResult scanResult = new ClassGraph()
				.addClassLoader(ClassLoaderTools.urlClassLoader(true, false, true, false, false)).enableAnnotationInfo()
				.scan()) {
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
