package com.x.server.console;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.DeploymentManager;

import com.x.base.core.project.AssembleA;
import com.x.base.core.project.CoreA;
import com.x.base.core.project.ServiceA;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.server.Servers;

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
						String json = IOUtils.toString(inputStream);
						CommandObject commandObject = XGsonBuilder.instance().fromJson(json, CommandObject.class);
						if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEncrypt())) {
							String decrypt = Crypto.rsaDecrypt(commandObject.getCredential(), Config.privateKey());
							if (!StringUtils.startsWith(decrypt, "o2@")) {
								IOUtils.write("error decrypt!", outputStream);
								continue;
							}
						}
						matcher = redeploy_pattern.matcher(commandObject.getCommand());
						if (matcher.find()) {
							String result = this.redeploy(matcher.group(1), commandObject.getBody());
							IOUtils.write(result, outputStream);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String redeploy(String name, String body) {
		String result = "success";
		try {
			logger.print("redeploy:{}.", name);
			Class<?> cls = Class.forName("com.x.base.core.project." + name);
			if (AssembleA.class.isAssignableFrom(cls)) {
				byte[] bytes = Base64.decodeBase64(body);
				File file = new File(Config.base(), "store/" + cls.getSimpleName() + ".war");
				FileUtils.writeByteArrayToFile(file, bytes, false);
				if (Servers.applicationServerIsRunning()) {
					DeploymentManager deployer = Servers.applicationServer.getBean(DeploymentManager.class);
					for (App app : deployer.getApps()) {
						if (StringUtils.equals("/" + cls.getSimpleName(), app.getContextPath())) {
							app.getContextHandler().stop();
							app.getContextHandler().start();
						}
					}
				}
			} else if (CoreA.class.isAssignableFrom(cls)) {
				byte[] bytes = Base64.decodeBase64(body);
				File file = new File(Config.base(), "store/jars/" + cls.getSimpleName() + ".jar");
				FileUtils.writeByteArrayToFile(file, bytes);
				if (Servers.applicationServerIsRunning()) {
					DeploymentManager deployer = Servers.applicationServer.getBean(DeploymentManager.class);
					List<String> dependWithContexts = new ArrayList<>();
					for (Class<AssembleA> a : AssembleA.dependWith(cls)) {
						dependWithContexts.add("/" + a.getSimpleName());
					}
					System.out.println(XGsonBuilder.toJson(dependWithContexts));
					if (ListTools.isNotEmpty(dependWithContexts)) {
						for (App app : deployer.getApps()) {
							if (dependWithContexts.contains(app.getContextPath())) {
								app.getContextHandler().stop();
								app.getContextHandler().start();
							}
						}
					}
				}
			} else if (ServiceA.class.isAssignableFrom(cls)) {
				byte[] bytes = Base64.decodeBase64(body);
				File file = new File(Config.base(), "store/" + cls.getSimpleName() + ".war");
				FileUtils.writeByteArrayToFile(file, bytes, false);
				if (Servers.applicationServerIsRunning()) {
					DeploymentManager deployer = Servers.applicationServer.getBean(DeploymentManager.class);
					for (App app : deployer.getApps()) {
						if (StringUtils.equals("/" + cls.getSimpleName(), app.getContextPath())) {
							app.getContextHandler().stop();
							app.getContextHandler().start();
						}
					}
				}
			}
		} catch (Exception e) {
			result = e.getMessage();
		}
		return result;
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
