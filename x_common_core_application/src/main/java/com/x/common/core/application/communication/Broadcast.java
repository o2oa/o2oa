package com.x.common.core.application.communication;

import com.x.base.core.gson.GsonPropertyObject;

public class Broadcast extends GsonPropertyObject {

	private String address;

	private String centerHost;
	private String centerContext;
	private Integer centerPort;

	private String applicationsToken;
	private String ftpsToken;

	private String cipher;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCenterHost() {
		return centerHost;
	}

	public void setCenterHost(String centerHost) {
		this.centerHost = centerHost;
	}

	public Integer getCenterPort() {
		return centerPort;
	}

	public void setCenterPort(Integer centerPort) {
		this.centerPort = centerPort;
	}

	public String getApplicationsToken() {
		return applicationsToken;
	}

	public void setApplicationsToken(String applicationsToken) {
		this.applicationsToken = applicationsToken;
	}

	public String getFtpsToken() {
		return ftpsToken;
	}

	public void setFtpsToken(String ftpsToken) {
		this.ftpsToken = ftpsToken;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public String getCenterContext() {
		return centerContext;
	}

	public void setCenterContext(String centerContext) {
		this.centerContext = centerContext;
	}

}
