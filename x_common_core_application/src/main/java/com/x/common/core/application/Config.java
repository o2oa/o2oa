package com.x.common.core.application;

import com.x.base.core.gson.GsonPropertyObject;

public class Config extends GsonPropertyObject {

	private String applicationServer;

	public String getApplicationServer() {
		return applicationServer;
	}

	public void setApplicationServer(String applicationServer) {
		this.applicationServer = applicationServer;
	}

}
