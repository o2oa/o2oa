package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Meeting extends GsonPropertyObject {

	public Meeting() {
		this.enable = false;
		this.oauth2Id = "5";
		this.port = 5080;
		this.host = "127.0.0.1";
		this.user = "xadmin";
		this.pass = "";
	}

	public static Meeting defaultInstance() {
		return new Meeting();
	}

	private Boolean enable;
	private String oauth2Id;
	private Integer port;
	private String host;
	private String user;
	private String pass;
	private String httpProtocol;

	public String getHttpProtocol() {
		return StringUtils.equalsIgnoreCase("https", this.httpProtocol) ? "https" : "http";
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable) ? true : false;
	}

	public String getOauth2Id() {
		return StringUtils.isEmpty(this.oauth2Id) ? "4" : this.oauth2Id;
	}

	public Integer getPort() {
		return this.port == null ? 5080 : this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setOauth2Id(String oauth2Id) {
		this.oauth2Id = oauth2Id;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
