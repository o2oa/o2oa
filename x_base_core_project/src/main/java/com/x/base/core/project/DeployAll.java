package com.x.base.core.project;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Packages;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.gson.XGsonBuilder;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class DeployAll {

	public static void main(String[] args) throws Exception {
		String json = StringUtils.replace(args[0], "\\", "/");
		Argument arg = XGsonBuilder.instance().fromJson(json, Argument.class);
		manualPackDeployAll(arg);
	}

	public static void manualPackDeployAll(Argument arg) throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		if (arg.getIncludeAssemble()) {
			for (String str : scanResult.getNamesOfSubclassesOf(Assemble.class)) {
				Assemble o = (Assemble) Class.forName(str).newInstance();
				if (!excluded(arg, o.getClass().getSimpleName())) {
					String dist = arg.getDistPath() + "/" + o.getName();
					System.out.println("pack name:" + o.getName() + ", distPath:" + dist);
					String warPath = o.pack(arg.getDistPath(), arg.getRepositoryPath());
					String context = "/" + o.getName();
					Tomcat8Deploy.deploy(warPath, arg.getServer(), arg.getPort(), arg.getUsername(), arg.getPassword(),
							context);
				} else {
					System.out.println("skip:" + o.getName());
				}
			}
		}
		if (arg.getIncludeService()) {
			for (String str : scanResult.getNamesOfSubclassesOf(Service.class)) {
				Service o = (Service) Class.forName(str).newInstance();
				if (!excluded(arg, o.getClass().getSimpleName())) {
					String dist = arg.getDistPath() + "/" + o.getName();
					System.out.println("pack name:" + o.getName() + ", distPath:" + dist);
					String warPath = o.pack(arg.getDistPath(), arg.getRepositoryPath());
					String context = "/" + o.getName();
					Tomcat8Deploy.deploy(warPath, arg.getServer(), arg.getPort(), arg.getUsername(), arg.getPassword(),
							context);
				} else {
					System.out.println("skip:" + o.getName());
				}
			}
		}
	}

	private static Boolean excluded(Argument arg, String name) throws Exception {
		if (null != arg.getExcludes()) {
			if (arg.getExcludes().contains(name)) {
				return true;
			}
		}
		return false;
	}

	public class Argument extends GsonPropertyObject {

		private String distPath;
		private String repositoryPath;
		private String centerHost;
		private Integer centerPort;
		private String centerCipher;
		private String configApplicationServer;
		private String server;
		private Integer port;
		private String username;
		private String password;
		private Boolean includeAssemble;
		private Boolean includeService;
		private List<String> excludes;

		public String getDistPath() {
			return distPath;
		}

		public void setDistPath(String distPath) {
			this.distPath = distPath;
		}

		public String getRepositoryPath() {
			return repositoryPath;
		}

		public void setRepositoryPath(String repositoryPath) {
			this.repositoryPath = repositoryPath;
		}

		public String getCenterHost() {
			return centerHost;
		}

		public void setCenterHost(String centerHost) {
			this.centerHost = centerHost;
		}

		public Integer getCenterPort() {
			return centerPort;
		}

		public void setCenterPort(Integer centerPort) {
			this.centerPort = centerPort;
		}

		public String getServer() {
			return server;
		}

		public void setServer(String server) {
			this.server = server;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Boolean getIncludeAssemble() {
			return includeAssemble;
		}

		public void setIncludeAssemble(Boolean includeAssemble) {
			this.includeAssemble = includeAssemble;
		}

		public Boolean getIncludeService() {
			return includeService;
		}

		public void setIncludeService(Boolean includeService) {
			this.includeService = includeService;
		}

		public List<String> getExcludes() {
			return excludes;
		}

		public void setExcludes(List<String> excludes) {
			this.excludes = excludes;
		}

		public String getCenterCipher() {
			return centerCipher;
		}

		public void setCenterCipher(String centerCipher) {
			this.centerCipher = centerCipher;
		}

		public String getConfigApplicationServer() {
			return configApplicationServer;
		}

		public void setConfigApplicationServer(String configApplicationServer) {
			this.configApplicationServer = configApplicationServer;
		}

	}
}
