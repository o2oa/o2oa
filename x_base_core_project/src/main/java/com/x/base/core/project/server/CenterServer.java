package com.x.base.core.project.server;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.Host;

public class CenterServer extends GsonPropertyObject {

	private static final Integer default_port = 20030;
	private static final Integer default_scanInterval = 0;

	private Boolean sslEnable = false;

	private Boolean forceRedeploy = true;

	private Integer port = default_port;

	private String proxyHost = "";

	private Integer proxyPort = default_port;

	private Integer scanInterval = default_scanInterval;

	public Integer getScanInterval() {
		if (null != this.scanInterval && this.scanInterval > 0) {
			return this.scanInterval;
		}
		return default_scanInterval;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Boolean getForceRedeploy() {
		return BooleanUtils.isNotFalse(this.forceRedeploy);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0) {
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

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setForceRedeploy(Boolean forceRedeploy) {
		this.forceRedeploy = forceRedeploy;
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

}
