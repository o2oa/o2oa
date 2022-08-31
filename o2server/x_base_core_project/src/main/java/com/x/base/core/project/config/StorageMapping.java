package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.project.gson.GsonPropertyObject;

public class StorageMapping extends GsonPropertyObject {

	private StorageProtocol protocol;
	private String username;
	private String password;
	private String host;
	private Integer port;
	private String prefix;
	private Integer weight;
	private String name;
	private Boolean deepPath;

	public StorageMapping() {

	}

	public StorageMapping(ExternalStorageSource source) {
		this.protocol = source.getProtocol();
		this.username = source.getUsername();
		this.password = source.getPassword();
		this.host = source.getHost();
		this.port = source.getPort();
		this.prefix = source.getPrefix();
		this.weight = source.getWeight();
		this.name = source.getName();
		this.deepPath = source.getDeepPath();

	}

	/* 默认是false */
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	public StorageProtocol getProtocol() {
		return protocol;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getPrefix() {
		return prefix;
	}

	public Integer getWeight() {
		return weight == null ? 1 : weight;
	}

	public String getName() {
		return name;
	}

	public void setProtocol(StorageProtocol protocol) {
		this.protocol = protocol;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

}
