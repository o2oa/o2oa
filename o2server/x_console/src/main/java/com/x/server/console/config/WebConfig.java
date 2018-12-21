package com.x.server.console.config;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;

public class WebConfig {

	private Integer port;

	private Boolean sslEnable;

	public static WebConfig read(String base) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String json = FileUtils.readFileToString(new File(base, "config/webServerConfig.json"), "UTF-8");
		return gson.fromJson(json, WebConfig.class);
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Boolean getSslEnable() {
		return sslEnable;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

}
