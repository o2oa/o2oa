package com.x.program.center.jaxrs.distribute;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutWebServer extends GsonPropertyObject {

	private Boolean sslEnable;
	private String host;
	private Integer port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Boolean getSslEnable() {
		return sslEnable;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
