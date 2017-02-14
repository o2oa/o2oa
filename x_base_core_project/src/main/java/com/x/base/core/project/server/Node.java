package com.x.base.core.project.server;

import com.x.base.core.gson.GsonPropertyObject;

public class Node extends GsonPropertyObject {

	private Boolean enable;

	private Boolean isPrimaryCenter = false;

	private ApplicationServer application;

	private WebServer web;

	private DataServer data;

	private String logLevel;

	private StorageServer storage;

	public Boolean getIsPrimaryCenter() {
		return isPrimaryCenter;
	}

	public void setIsPrimaryCenter(Boolean isPrimaryCenter) {
		this.isPrimaryCenter = isPrimaryCenter;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public ApplicationServer getApplication() {
		return application;
	}

	public void setApplication(ApplicationServer application) {
		this.application = application;
	}

	public WebServer getWeb() {
		return web;
	}

	public void setWeb(WebServer web) {
		this.web = web;
	}

	public DataServer getData() {
		return data;
	}

	public void setData(DataServer data) {
		this.data = data;
	}

	public StorageServer getStorage() {
		return storage;
	}

	public void setStorage(StorageServer storage) {
		this.storage = storage;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

}
