package com.x.common.core.application.communication;

import com.x.base.core.gson.GsonPropertyObject;

public class Report extends GsonPropertyObject {

	private String className;
	private String applicationServer;
	private String token;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getApplicationServer() {
		return applicationServer;
	}

	public void setApplicationServer(String applicationServer) {
		this.applicationServer = applicationServer;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
