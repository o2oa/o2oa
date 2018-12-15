package com.x.base.core.project.config;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.project.gson.GsonPropertyObject;

public class StorageMapping extends GsonPropertyObject {

	private StorageProtocol protocol;
	private String username;
	private String password;
	private String host;
	private Integer port;

	private Boolean enable;
	private Integer weight;
	/* 关联到storageServer的name */
	private String name;

	public String getPassword() {
		// return StringUtils.isEmpty(this.password) ? "" :
		// UriParser.encode(this.password);
		return this.password;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StorageProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(StorageProtocol protocol) {
		this.protocol = protocol;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
