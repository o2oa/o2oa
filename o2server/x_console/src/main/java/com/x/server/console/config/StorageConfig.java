package com.x.server.console.config;

import java.util.HashMap;
import java.util.Map;

public class StorageConfig {

	private Integer port;
	private Boolean sslEnable;
	private Map<String, String> users = new HashMap<String, String>();

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

	public Map<String, String> getUsers() {
		return users;
	}

	public void setUsers(Map<String, String> users) {
		this.users = users;
	}

}
