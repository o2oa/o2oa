package com.x.base.core.project.server;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.Host;

public class WebServer extends GsonPropertyObject {

	public static WebServer defaultInstance() {
		return new WebServer();
	}

	public WebServer() {
		this.enable = true;
		this.port = default_port;
		this.sslEnable = false;
		this.proxyHost = "";
		this.proxyPort = default_port;
		this.weight = default_weight;
	}

	private static final Integer default_port = 80;
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
		if (null != this.port && this.port > 0) {
			return this.port;
		}
		return default_port;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() {
		if (StringUtils.isNotEmpty(this.proxyHost)) {
			return this.proxyHost;
		}
		return Host.ROLLBACK_IPV4;
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return default_port;
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

}
