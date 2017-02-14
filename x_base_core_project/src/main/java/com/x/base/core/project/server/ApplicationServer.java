package com.x.base.core.project.server;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.utils.Host;

public class ApplicationServer extends GsonPropertyObject {

	private static final Integer default_port = 20020;
	private static final Integer default_scanInterval = 0;
	private static final Integer default_weight = 100;

	private Boolean enable = false;
	private Integer port = default_port;
	private Boolean sslEnable = false;
	private String proxyHost = "";
	private Integer proxyPort = default_port;
	private Boolean forceRedeploy = true;
	private Integer scanInterval = default_scanInterval;

	private CopyOnWriteArrayList<NameWeightPair> projects = new CopyOnWriteArrayList<>();

	public Integer getScanInterval() {
		if (null != this.scanInterval && this.scanInterval > 0) {
			return this.scanInterval;
		}
		return default_scanInterval;
	}

	public class NameWeightPair {

		private String name;

		private Integer weight = default_weight;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getWeight() {
			if ((null != this.weight) && (this.weight > 0)) {
				return this.weight;
			}
			return default_weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

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

	public Boolean getForceRedeploy() {
		return BooleanUtils.isNotFalse(this.forceRedeploy);
	}

	public CopyOnWriteArrayList<NameWeightPair> getProjects() {
		if (null == this.projects) {
			return new CopyOnWriteArrayList<NameWeightPair>();
		}
		return this.projects;
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

	public void setForceRedeploy(Boolean forceRedeploy) {
		this.forceRedeploy = forceRedeploy;
	}

	public void setScanInterval(Integer scanInterval) {
		this.scanInterval = scanInterval;
	}

	public void setProjects(CopyOnWriteArrayList<NameWeightPair> projects) {
		this.projects = projects;
	}

}
