package com.x.program.init;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.init.Missions.Mission;

public class MissionSetSecret implements Mission {

	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public void execute(Missions.Messages messages) {
		messages.head(MissionSetSecret.class.getSimpleName());
		try {
			messages.msg("executing");
			this.changeInternalDataServerPassword(Config.token().getPassword(), getSecret());
			this.changeTokenPassword(getSecret());
			Config.resource_commandQueue().add("ctl -flushConfig");
			Config.resource_commandQueue().add("ctl -initResourceFactory");
			Config.regenerate();
			// 命令队列是用多线程运行的,后续如果有ctl -initResourceFactory对目录有操作,可能导致重复删除目录冲突.
			Thread.sleep(5000);
			messages.msg("success");
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			messages.err(e.getMessage());
			throw new ExceptionMissionExecute(e);
		}
	}

	private void changeInternalDataServerPassword(String oldPassword, String newPassword)
			throws IOException, URISyntaxException, SQLException {
		org.h2.Driver.load();
		Path path = Config.pathLocalRepositoryData(true).resolve(H2Tools.FILENAME_DATABASE);
		if (Files.exists(path)) {
			try (Connection conn = DriverManager.getConnection("jdbc:h2:"
					+ Config.pathLocalRepositoryData(true).resolve(H2Tools.DATABASE).toAbsolutePath().toString(),
					H2Tools.USER, oldPassword)) {
				RunScript.execute(conn,
						new StringReader("ALTER USER " + H2Tools.USER + " SET PASSWORD '" + newPassword + "'"));
			} catch (SQLException e) {
				throw new ExceptionMissionExecute("本地H2数据库密码修改失败,数据库文件损坏或旧密码不匹配.");
			}
		}
	}

	private void changeTokenPassword(String secret) throws Exception {
		Config.token().setPassword(secret);
		Config.token().save();
		Config.flush();
	}

	public static boolean check() {
		JsonObject jsonObject = BaseTools.readConfigObject(Config.PATH_CONFIG_TOKEN, JsonObject.class);
		String value = XGsonBuilder.extractString(jsonObject, "password");
		return StringUtils.isBlank(value);
	}

}
