package com.x.base.core.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Application extends GsonPropertyObject {

	private String name;
	private String node;
	private String contextPath;
	private Integer port;
	private String token;
	private Boolean sslEnable;
	private List<String> containerEntities = new ArrayList<>();

	private String proxyHost;
	private Integer proxyPort;

	private Integer weight;

	private Date reportDate;

	public String getUrlRoot() {
		StringBuffer buffer = new StringBuffer();
		if (BooleanUtils.isTrue(this.sslEnable)) {
			buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
					.append(port == 443 ? "" : (":" + port));
		} else {
			buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
					.append(port == 80 ? "" : (":" + port));
		}
		buffer.append(contextPath + "/jaxrs/");
		return buffer.toString();

	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public Boolean getSslEnable() {
		return sslEnable;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

 

	public List<String> getContainerEntities() {
		return containerEntities;
	}

	public void setContainerEntities(List<String> containerEntities) {
		this.containerEntities = containerEntities;
	}

}