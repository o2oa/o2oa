package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WebServer extends GsonPropertyObject {

	public static WebServer defaultInstance() {
		return new WebServer();
	}

	public WebServer() {
		this.enable = true;
		this.port = null;
		this.sslEnable = false;
		this.proxyHost = "";
		this.proxyPort = null;
		this.weight = default_weight;
	}

	private static final Integer default_http_port = 80;
	private static final Integer default_https_port = 443;
	private static final Integer default_weight = 100;

	private Boolean enable;
	private Integer port;
	private Boolean sslEnable;
	private String proxyHost;
	private Integer proxyPort;
	private Integer weight;

	public Integer getWeight() {
		if (weight == null || weight < 0) {
			return default_weight;
		}
		return weight;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		} else {
			if (this.getSslEnable()) {
				return default_https_port;
			} else {
				return default_http_port;
			}

		}
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() throws Exception {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0 && this.proxyPort < 65535) {
			return this.proxyPort;
		} else {
			return this.getPort();
		}
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	// public void setHost(String host) {
	// this.host = host;
	// }

}
