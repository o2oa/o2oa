package com.x.server.console;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.server.console.server.Servers;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class NodeAgent extends Thread {

	private static Logger logger = LoggerFactory.getLogger(NodeAgent.class);

	public NodeAgent() {
	}

	public static final Pattern redeploy_pattern = Pattern.compile("^redeploy:(.+)$", Pattern.CASE_INSENSITIVE);

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(Config.currentNode().nodeAgentPort())) {
			Matcher matcher;
			while (true) {
				try (Socket client = serverSocket.accept()) {
					try (OutputStream outputStream = client.getOutputStream();
							InputStream inputStream = client.getInputStream()) {
						String json = IOUtils.toString(inputStream, DefaultCharset.charset_utf_8);
						CommandObject commandObject = XGsonBuilder.instance().fromJson(json, CommandObject.class);
						if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEncrypt())) {
							String decrypt = Crypto.rsaDecrypt(commandObject.getCredential(), Config.privateKey());
							if (!StringUtils.startsWith(decrypt, "o2@")) {
								IOUtils.write("error decrypt!", outputStream, DefaultCharset.charset_utf_8);
								continue;
							}
						}
						matcher = redeploy_pattern.matcher(commandObject.getCommand());
						if (matcher.find()) {
							byte[] bytes = Base64.decodeBase64(commandObject.getBody());
							String result = this.redeploy(matcher.group(1), bytes);
							IOUtils.write(result, outputStream, DefaultCharset.charset_utf_8);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String redeploy(String name, byte[] bytes) {
		String result = "success";
		try {
			logger.print("redeploy:{}.", name);
			switch (this.type(name)) {
			case "storeWar":
				storeWar(name, bytes);
				break;
			case "storeJar":
				storeJar(name, bytes);
				break;
			case "customWar":
				customWar(name, bytes);
				break;
			case "customJar":
				customJar(name, bytes);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		return result;
	}

	private void storeWar(String simpleName, byte[] bytes) throws Exception {
		ClassInfo classInfo = this.scanModuleClassInfo(simpleName);
		Class<?> cls = Class.forName(classInfo.getName());
		Module module = cls.getAnnotation(Module.class);
		File war = new File(Config.dir_store(), cls.getSimpleName() + ".war");
		FileUtils.writeByteArrayToFile(war, bytes);
		if (Objects.equals(module.type(), ModuleType.CENTER)) {
			File dir = new File(Config.dir_servers_centerServer_work(), cls.getSimpleName());
			if (Servers.centerServerIsRunning()) {
				GzipHandler gzipHandler = (GzipHandler) Servers.centerServer.getHandler();
				HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
				for (Handler handler : hanlderList.getHandlers()) {
					if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
						QuickStartWebApp app = (QuickStartWebApp) handler;
						if (StringUtils.equals("/" + cls.getSimpleName(), app.getContextPath())) {
							app.stop();
							this.modified(bytes, war, dir);
							app.start();
						}
					}
				}
			}
		} else {
			File dir = new File(Config.dir_servers_applicationServer_work(), cls.getSimpleName());
			war = new File(Config.dir_store(), cls.getSimpleName() + ".war");
			FileUtils.writeByteArrayToFile(war, bytes, false);
			if (Servers.applicationServerIsRunning()) {
				GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
				HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
				for (Handler handler : hanlderList.getHandlers()) {
					if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
						QuickStartWebApp app = (QuickStartWebApp) handler;
						if (StringUtils.equals("/" + cls.getSimpleName(), app.getContextPath())) {
							app.stop();
							this.modified(bytes, war, dir);
							app.start();
						}
					}
				}
			}
		}
	}

	private void storeJar(String simpleName, byte[] bytes) throws Exception {
		File jar = new File(Config.dir_store_jars(true), simpleName + ".jar");
		FileUtils.writeByteArrayToFile(jar, bytes, false);
		List<ClassInfo> classInfos = this.listModuleDependencyWith(simpleName);
		List<String> contextPaths = new ArrayList<>();
		for (ClassInfo info : classInfos) {
			contextPaths.add("/" + info.getSimpleName());
		}
		if (Servers.applicationServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (contextPaths.contains(app.getContextPath())) {
						logger.print("{} need restart because {} redeployed.", app.getDisplayName(), simpleName);
						app.stop();
					}
				}
			}
		}
		if (Servers.centerServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.centerServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (contextPaths.contains(app.getContextPath())) {
						logger.print("{} need restart because {} redeployed.", app.getDisplayName(), simpleName);
						app.stop();
					}
				}
			}
		}
		if (Servers.applicationServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (contextPaths.contains(app.getContextPath())) {
						logger.print("{} need restart because {} redeployed.", app.getDisplayName(), simpleName);
						app.start();
					}
				}
			}
		}
		if (Servers.centerServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.centerServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (contextPaths.contains(app.getContextPath())) {
						logger.print("{} need restart because {} redeployed.", app.getDisplayName(), simpleName);
						app.start();
					}
				}
			}
		}
	}

	private void customWar(String simpleName, byte[] bytes) throws Exception {
		File war = new File(Config.dir_custom(true), simpleName + ".war");
		File dir = new File(Config.dir_servers_applicationServer_work(), simpleName);
		FileUtils.writeByteArrayToFile(war, bytes, false);
		if (Servers.applicationServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (StringUtils.equals("/" + simpleName, app.getContextPath())) {
						app.stop();
						this.modified(bytes, war, dir);
						app.start();
					}
				}
			}
		}
	}

	private void customJar(String simpleName, byte[] bytes) throws Exception {
		File jar = new File(Config.dir_custom_jars(true), simpleName + ".jar");
		FileUtils.writeByteArrayToFile(jar, bytes, false);
		List<String> contexts = new ArrayList<>();
		for (String s : Config.dir_custom().list(new WildcardFileFilter("*.war"))) {
			contexts.add("/" + FilenameUtils.getBaseName(s));
		}
		if (Servers.applicationServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (contexts.contains(app.getContextPath())) {
						app.stop();
						Thread.sleep(2000);
						app.start();
					}
				}
			}
		}
	}

	private List<ClassInfo> listModuleDependencyWith(String name) throws Exception {
		List<ClassInfo> list = new ArrayList<>();
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				Class<?> cls = Class.forName(info.getName());
				Module module = cls.getAnnotation(Module.class);
				if (Objects.equals(module.type(), ModuleType.ASSEMBLE)
						|| Objects.equals(module.type(), ModuleType.SERVICE)
						|| Objects.equals(module.type(), ModuleType.CENTER)) {
					if (ArrayUtils.contains(module.storeJars(), name) || ArrayUtils.contains(module.customJars(), name)
							|| ArrayUtils.contains(module.dynamicJars(), name)) {
						list.add(info);
					}
				}
			}
		}
		return list;
	}

	private ClassInfo scanModuleClassInfo(String name) throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				Class<?> clz = Class.forName(info.getName());
				if (StringUtils.equals(clz.getSimpleName(), name)) {
					return info;
				}
			}
			return null;
		}
	}

	private void modified(byte[] bytes, File war, File dir) throws Exception {
		File lastModified = new File(dir, "WEB-INF/lastModified");
		if ((!lastModified.exists()) || lastModified.isDirectory() || (war.lastModified() != NumberUtils
				.toLong(FileUtils.readFileToString(lastModified, DefaultCharset.charset_utf_8), 0))) {
			if (dir.exists()) {
				FileUtils.forceDelete(dir);
			}
			JarTools.unjar(bytes, "", dir, true);
			FileUtils.writeStringToFile(lastModified, war.lastModified() + "", DefaultCharset.charset_utf_8, false);
		}
	}

	public static class CommandObject {

		private String command;

		private String body;

		private String credential;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

	}

	private String type(String simpleName) throws Exception {
		if ((new File(Config.dir_store(), simpleName + ".war")).exists()) {
			return "storeWar";
		}
		if ((new File(Config.dir_store_jars(), simpleName + ".jar")).exists()) {
			return "storeJar";
		}
		if ((new File(Config.dir_custom(), simpleName + ".war")).exists()) {
			return "customWar";
		}
		if ((new File(Config.dir_custom_jars(), simpleName + ".jar")).exists()) {
			return "customJar";
		}
		return null;
	}

}
