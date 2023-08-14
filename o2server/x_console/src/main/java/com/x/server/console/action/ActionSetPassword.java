package com.x.server.console.action;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;
import com.x.server.console.server.Servers;

public class ActionSetPassword extends ActionBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSetPassword.class);

	public boolean execute(String oldPassword, String newPassword) throws Exception {
		/** 如果初始密码没有修改就设置为初始密码 */
		if (StringUtils.equals(Config.token().getPassword(), Token.initPassword)) {
			oldPassword = Token.initPassword;
		}
		if (!StringUtils.equals(Config.token().getPassword(), oldPassword)) {
			LOGGER.print("old password not match.");
			return false;
		}
		this.changeInternalDataServerPassword(oldPassword, newPassword);
		Config.token().setPassword(newPassword);
		Config.token().save();
		LOGGER.print("The initial manager password has been modified.");
		return true;
	}

	private void changeInternalDataServerPassword(String oldPassword, String newPassword) throws Exception {
		org.h2.Driver.load();
		DataServer dataServer = Config.currentNode().getData();
		if (Servers.dataServerIsRunning()) {
			try (Connection conn = DriverManager.getConnection(
					"jdbc:h2:tcp://" + Config.currentNode() + ":" + dataServer.getTcpPort() + "/" + H2Tools.DATABASE,
					H2Tools.USER, oldPassword)) {
				RunScript.execute(conn,
						new StringReader("ALTER USER " + H2Tools.USER + " SET PASSWORD '" + newPassword + "'"));
			}
		} else {
			Path path = Config.pathLocalRepositoryData(true).resolve(H2Tools.DATABASE);
			if (Files.exists(path)) {
				try (Connection conn = DriverManager.getConnection("jdbc:h2:" + path.toAbsolutePath().toString(),
						H2Tools.USER, oldPassword)) {
					RunScript.execute(conn,
							new StringReader("ALTER USER " + H2Tools.USER + " SET PASSWORD '" + newPassword + "'"));
				}
			}
		}
		Config.resource_commandQueue().add("ctl -initResourceFactory");
	}
}
