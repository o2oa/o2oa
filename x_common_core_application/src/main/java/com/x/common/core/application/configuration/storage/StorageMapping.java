package com.x.common.core.application.configuration.storage;

import com.x.base.core.gson.GsonPropertyObject;

public class StorageMapping extends GsonPropertyObject {

	private StorageServiceType storageServiceType;
	private Boolean enable;
	private Integer weight;
	private String name;

	private String ftpHost;
	private Integer ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpPath;

	public StorageServiceType getStorageServiceType() {
		return storageServiceType;
	}

	public void setStorageServiceType(StorageServiceType storageServiceType) {
		this.storageServiceType = storageServiceType;
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

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

}
