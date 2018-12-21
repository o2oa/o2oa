package com.x.server.console.action;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSetPassword extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionSetPassword.class);

	public boolean execute(String oldPassword, String newPassword) throws Exception {
		/** 如果初始密码没有修改就设置为初始密码 */
		if (StringUtils.equals(Config.token().getPassword(), Token.initPassword)) {
			oldPassword = Token.initPassword;
		}
		if (!StringUtils.equals(Config.token().getPassword(), oldPassword)) {
			logger.print("old password not match.");
			return false;
		}
		this.changeInternalDataServerPassword(oldPassword, newPassword);
		Config.token().setPassword(newPassword);
		Config.token().save();
		logger.print("The initial manager password has been modified.");
		return true;
	}

	private void changeInternalDataServerPassword(String oldPassword, String newPassword) throws Exception {
		org.h2.Driver.load();
		for (Entry<String, DataServer> en : Config.nodes().dataServers().entrySet()) {
			DataServer o = en.getValue();
			if (BooleanUtils.isTrue(o.getEnable())) {
				try (Connection conn = DriverManager.getConnection(
						"jdbc:h2:tcp://" + en.getKey() + ":" + o.getTcpPort() + "/X", "sa", oldPassword)) {
					RunScript.execute(conn, new StringReader("ALTER USER SA SET PASSWORD '" + newPassword + "'"));
				} catch (Exception e) {
					throw new Exception("Verify that the dataServer:" + en.getKey()
							+ " is started and that the dataServer password is updated synchronously.", e);
				}
			}
		}
	}
}
