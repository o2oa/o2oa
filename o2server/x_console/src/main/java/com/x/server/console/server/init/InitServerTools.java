package com.x.server.console.server.init;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.x.base.core.project.x_program_init;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.JarTools;
import com.x.base.core.project.tools.PathTools;
import com.x.server.console.server.JettySeverTools;

public class InitServerTools extends JettySeverTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitServerTools.class);

	public static Server start() throws Exception {

		ApplicationServer applicationServer = Config.currentNode().getApplication();

		cleanWorkDirectory();

		Path war = Paths.get(Config.dir_store().toString(), x_program_init.class.getSimpleName() + PathTools.DOT_WAR);
		Path dir = Config.path_servers_initServer_work(true).resolve(x_program_init.class.getSimpleName());

		modified(war, dir);

		return startStandalone(applicationServer);

	}

	private static Server startStandalone(ApplicationServer applicationServer) throws Exception {
		HandlerList handlers = new HandlerList();
		QuickStartWebApp webApp = webContext();
		handlers.addHandler(webApp);
		handlers.addHandler(new CacheControlHandler());
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("AdminServerQueuedThreadPool");
		threadPool.setMinThreads(THREAD_POOL_SIZE_MIN);
		threadPool.setMaxThreads(THREAD_POOL_SIZE_MAX);
		Server server = new Server(threadPool);
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", MAX_FORM_CONTENT_SIZE);
		webApp.setAttribute(Server.class.getName(), server);
		if (BooleanUtils.isTrue(applicationServer.getSslEnable())) {
			addHttpsConnector(server, applicationServer.getPort(), true);
		} else {
			addHttpConnector(server, applicationServer.getPort(), true);
		}
		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handlers);
		server.setHandler(gzipHandler);
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);
		server.start();

		InetAddress addr = InetAddress.getLocalHost();

		LOGGER.print("请通过{}服务访问{}端口来初始化服务器密码,本机地址:{}, 访问地址:{}.",
				BooleanUtils.isTrue(applicationServer.getSslEnable()) ? "https" : "http", applicationServer.getPort(),
				InetAddress.getLocalHost(), url(applicationServer, addr.getHostAddress()));
		return server;

	}

	/**
	 * 计算可能的访问地址
	 * 
	 * @param applicationServer
	 * @param host
	 * @return
	 */
	private static String url(ApplicationServer applicationServer, String host) {
		StringBuilder builder = new StringBuilder();
		if (BooleanUtils.isTrue(applicationServer.getSslEnable())) {
			builder.append("https://");
		} else {
			builder.append("http://");
		}
		builder.append(host);
		if (!((applicationServer.getSslEnable() && (applicationServer.getPort() == 443))
				|| (BooleanUtils.isNotTrue(applicationServer.getSslEnable()) && (applicationServer.getPort() == 80)))) {
			builder.append(":");
			builder.append(applicationServer.getPort());
		}
		return builder.toString();
	}

	public static QuickStartWebApp webContext() throws Exception {
		Path dir = Config.path_servers_initServer_work(true).resolve(x_program_init.class.getSimpleName());
		QuickStartWebApp webApp = new QuickStartWebApp();
		webApp.setAutoPreconfigure(false);
		webApp.setDisplayName(x_program_init.class.getSimpleName());
		webApp.setContextPath("/");
		ResourceCollection resources = new ResourceCollection(new String[] { dir.toAbsolutePath().toString(),
				Config.path_servers_webServer_x_init(true).toAbsolutePath().toString() });
		webApp.setBaseResource(resources);
		webApp.setDescriptor(dir.resolve(Paths.get(PathTools.WEB_INF_WEB_XML)).toString());
		webApp.setExtraClasspath(calculateExtraClassPath(x_program_init.class));
		Path ext = dir.resolve("WEB-INF").resolve("ext");
		if (Files.exists(ext)) {
			webApp.setExtraClasspath(calculateExtraClassPath(x_program_init.class, ext));
		} else {
			webApp.setExtraClasspath(calculateExtraClassPath(x_program_init.class));
		}
		LOGGER.debug("{} extra class path:{}.", x_program_init.class, webApp.getExtraClasspath());
		webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", BooleanUtils.FALSE);
		webApp.getInitParams().put("org.eclipse.jetty.jsp.precompiled", BooleanUtils.TRUE);
		webApp.getInitParams().put("org.eclipse.jetty.servlet.Default.dirAllowed",
				BooleanUtils.toStringTrueFalse(false));
		webApp.setWelcomeFiles(new String[] { "index.html", "sample.html" });
		return webApp;
	}

	private static void cleanWorkDirectory() throws IOException, URISyntaxException {
		try (Stream<Path> paths = Files.walk(Config.path_servers_initServer_work(true))) {
			// 删除每个文件和子目录
			paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	private static void modified(Path war, Path dir) throws Exception {
		Path lastModified = Paths.get(dir.toString(), PathTools.WEB_INF_LASTMODIFIED);
		if ((!Files.exists(lastModified)) || Files.isDirectory(lastModified)
				|| (Files.getLastModifiedTime(war).toMillis() != NumberUtils
						.toLong(FileUtils.readFileToString(lastModified.toFile(), DefaultCharset.charset_utf_8), 0))) {
			LOGGER.info("deploy war:{}.", war.getFileName().toAbsolutePath());
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

	public static class CacheControlHandler extends AbstractHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			// 设置Cache-Control头部字段
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			// <code> baseRequest.setHandled(true)</code>;标记请求已经被处理
		}

	}
}