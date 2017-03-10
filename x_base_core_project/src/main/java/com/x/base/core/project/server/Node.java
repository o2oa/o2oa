package com.x.base.core.project.server;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.gson.GsonPropertyObject;

public class Node extends GsonPropertyObject {

	public static Node defaultInstance() {
		Node o = new Node();
		o.enable = true;
		o.isPrimaryCenter = true;
		o.application = ApplicationServer.defaultInstance();
		o.web = WebServer.defaultInstance();
		o.data = DataServer.defaultInstance();
		o.storage = StorageServer.	defaultInstance();
		o.logLevel = "info";
		return o;
	}

	private Boolean enable;
	private Boolean isPrimaryCenter;
	private ApplicationServer application;
	private WebServer web;
	private DataServer data;
	private StorageServer storage;
	private String logLevel;

	public Boolean getIsPrimaryCenter() {
		return BooleanUtils.isTrue(this.isPrimaryCenter);
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
