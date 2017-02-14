package com.x.base.core.project;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat8xRemoteContainer;
import org.codehaus.cargo.container.tomcat.Tomcat8xRemoteDeployer;
import org.codehaus.cargo.container.tomcat.TomcatRuntimeConfiguration;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.Host;

public class Tomcat8Deploy {

	public static void main(String[] args) throws Exception {
		String str = StringUtils.replace(args[0], "\\", "/");
		Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
		String context = "/" + FilenameUtils.getBaseName(arg.getFilePath());
		deploy(arg.getFilePath(), arg.getServer(), arg.getPort(), arg.getUsername(), arg.getPassword(), context);
	}

	public static void deploy(String filePath, String server, Integer port, String username, String password,
			String context) throws Exception {
		TomcatRuntimeConfiguration configuration = new TomcatRuntimeConfiguration();
		configuration.setProperty(GeneralPropertySet.HOSTNAME,
				StringUtils.isNotEmpty(server) ? server : Host.ROLLBACK_IPV4);
		configuration.setProperty(ServletPropertySet.PORT, port != null ? port.toString() : "20080");
		configuration.setProperty(RemotePropertySet.USERNAME, username);
		configuration.setProperty(RemotePropertySet.PASSWORD, password);
		Tomcat8xRemoteContainer container = new Tomcat8xRemoteContainer(configuration);
		Tomcat8xRemoteDeployer deployer = new Tomcat8xRemoteDeployer(container);
		WAR war = new WAR(filePath);
		war.setContext(context);
		if (existed(deployer, context)) {
			deployer.undeploy(war);
		}
		deployer.deploy(war);
	}

	private static boolean existed(Tomcat8xRemoteDeployer deployer, String context) {
		String str = deployer.list();
		for (String s : StringUtils.split(str, StringUtils.LF)) {
			if (StringUtils.startsWith(s, context + ":")) {
				return true;
			}
		}
		return false;
	}

	public class Argument extends GsonPropertyObject {

		private String filePath;
		private String server;
		private Integer port;
		private String username;
		private String password;

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
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
	}
}