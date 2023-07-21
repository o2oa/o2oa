package com.x.server.console.config;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;

public class DataServerConfig {

	private Integer tcpPort;
	private Integer webPort;

	private String password;
	private Boolean enableWebConsole;

	public static DataServerConfig read(String base) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String json = FileUtils.readFileToString(new File(base, "config/dataServerConfig.json"), "UTF-8");
		return gson.fromJson(json, DataServerConfig.class);
	}

	public Integer getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(Integer tcpPort) {
		this.tcpPort = tcpPort;
	}

	public Integer getWebPort() {
		return webPort;
	}

	public void setWebPort(Integer webPort) {
		this.webPort = webPort;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnableWebConsole() {
		return enableWebConsole;
	}

	public void setEnableWebConsole(Boolean enableWebConsole) {
		this.enableWebConsole = enableWebConsole;
	}

}
