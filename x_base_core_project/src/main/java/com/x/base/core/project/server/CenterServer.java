package com.x.base.core.project.server;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.Host;

public class CenterServer extends GsonPropertyObject {

	private static final Integer default_port = 20030;
	private static final Integer default_scanInterval = 0;

	public static CenterServer defaultInstance() {
		return new CenterServer();
	}

	public CenterServer() {
		this.sslEnable = false;
		this.redeploy = true;
		this.host = "";
		this.port = default_port;
		this.proxyHost = "";
		this.proxyPort = default_port;
		this.scanInterval = default_scanInterval;
	}

	private Boolean sslEnable;
	private Boolean redeploy;
	private String host;
	private Integer port;
	private String proxyHost;
	private Integer proxyPort;
	private Integer scanInterval;
	private String systemTitle;
	private LinkedHashMap<String, Object> config;

	public Integer getScanInterval() {
		if (null != this.scanInterval && this.scanInterval > 0) {
			return this.scanInterval;
		}
		return default_scanInterval;
	}

	public Boolean getRedeploy() {
		return BooleanUtils.isTrue(this.redeploy);
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return default_port;
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

	public String getSystemTitle() {
		if (StringUtils.isNotEmpty(this.systemTitle)) {
			return this.systemTitle;
		}
		return "企业办公平台";
	}

	public LinkedHashMap<String, Object> getConfig() {
		if (null == this.config) {
			return new LinkedHashMap<String, Object>();
		}
		return this.config;
	}

	public String getHost() {
		if (StringUtils.isNotEmpty(this.host)) {
			return this.host;
		}
		return "";
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setScanInterval(Integer scanInterval) {
		this.scanInterval = scanInterval;
	}

	public void setRedeploy(Boolean redeploy) {
		this.redeploy = redeploy;
	}

	public void setSystemTitle(String systemTitle) {
		this.systemTitle = systemTitle;
	}

	public void setConfig(LinkedHashMap<String, Object> config) {
		this.config = config;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
