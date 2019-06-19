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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
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
							String result = this.redeploy(matcher.group(1), commandObject.getBody());
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

	private String redeploy(String name, String body) {
		String result = "success";
		try {
			logger.print("redeploy:{}.", name);
			ClassInfo classInfo = this.scanModuleClassInfo(name);
			if (null == classInfo) {
				throw new Exception(String.format("module not exist:%s.", name));
			}
			Class<?> cls = Class.forName(classInfo.getName());
			Module module = cls.getAnnotation(Module.class);
			byte[] bytes = Base64.decodeBase64(body);
			if (Objects.equals(module.type(), ModuleType.ASSEMBLE)
					|| Objects.equals(module.type(), ModuleType.SERVICE)) {
				File war = null;
				File dir = new File(Config.dir_servers_applicationServer_work(), cls.getSimpleName());
				if (Objects.equals(ModuleCategory.OFFICIAL, module.category())) {
					war = new File(Config.dir_store(), cls.getSimpleName() + ".war");
				} else if (Objects.equals(ModuleCategory.CUSTOM, module.category())) {
					war = new File(Config.dir_custom(), cls.getSimpleName() + ".war");
				}
				FileUtils.writeByteArrayToFile(war, bytes, false);
				this.redeployAssembleAServiceA(cls, war, dir);
			} else if (Objects.equals(module.type(), ModuleType.CENTER)) {
				File war = new File(Config.dir_store(), cls.getSimpleName() + ".war");
				FileUtils.writeByteArrayToFile(war, bytes);
				File dir = new File(Config.dir_servers_centerServer_work(), cls.getSimpleName());
				this.redeployAssembleC(cls, war, dir);
			} else if (Objects.equals(module.type(), ModuleType.BASE)
					|| Objects.equals(module.type(), ModuleType.ENTITY)
					|| Objects.equals(module.type(), ModuleType.EXPRESS)) {
				File jar = null;
				if (Objects.equals(ModuleCategory.OFFICIAL, module.category())) {
					jar = new File(Config.dir_store_jars(), cls.getSimpleName() + ".jar");
				} else if (Objects.equals(ModuleCategory.CUSTOM, module.category())) {
					jar = new File(Config.dir_custom(), cls.getSimpleName() + ".jar");
				}
				FileUtils.writeByteArrayToFile(jar, bytes);
				this.redeployCoreA(cls);
			}
		} catch (Exception e) {
			result = e.getMessage();
		}
		return result;
	}

	private boolean redeployCoreA(Class<?> cls) throws Exception {
		List<ClassInfo> classInfos = this.listModuleDependencyWith(cls.getSimpleName());
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
						logger.print("{} need restart because {} redeployed.", app.getDisplayName(),
								cls.getSimpleName());
						app.stop();
						Thread.sleep(2000);
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
						logger.print("{} need restart because {} redeployed.", app.getDisplayName(),
								cls.getSimpleName());
						app.stop();
						Thread.sleep(2000);
						app.start();
					}
				}
			}
		}
		return true;
	}

	private boolean redeployAssembleAServiceA(Class<?> cls, File war, File dir) throws Exception {
		if (Servers.applicationServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (StringUtils.equals("/" + cls.getSimpleName(), app.getContextPath())) {
						app.stop();
						Thread.sleep(2000);
						this.modified(war, dir);
						app.start();
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean redeployAssembleC(Class<?> cls, File war, File dir) throws Exception {
		if (Servers.centerServerIsRunning()) {
			GzipHandler gzipHandler = (GzipHandler) Servers.centerServer.getHandler();
			HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
			for (Handler handler : hanlderList.getHandlers()) {
				if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
					QuickStartWebApp app = (QuickStartWebApp) handler;
					if (StringUtils.equals("/" + cls.getSimpleName(), app.getContextPath())) {
						app.stop();
						this.modified(war, dir);
						app.start();
						return true;
					}
				}
			}
		}
		return false;
	}

	public List<ClassInfo> listModuleDependencyWith(String name) throws Exception {
		List<ClassInfo> list = new ArrayList<>();
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				Class<?> cls = Class.forName(info.getName());
				Module module = cls.getAnnotation(Module.class);
				if (Objects.equals(module.type(), ModuleType.ASSEMBLE)
						|| Objects.equals(module.type(), ModuleType.SERVICE)
						|| Objects.equals(module.type(), ModuleType.CENTER)) {
					if (ArrayUtils.contains(module.storeJars(),name) || ArrayUtils.contains(module.customJars(),name)
							|| ArrayUtils.contains(module.dynamicJars(),name)) {
						list.add(info);
					}
				}
			}
		}
		return list;
	}

	public ClassInfo scanModuleClassInfo(String name) throws Exception {
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

	private void modified(File war, File dir) throws Exception {
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

}
