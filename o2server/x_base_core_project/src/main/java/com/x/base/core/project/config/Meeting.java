package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class Meeting extends ConfigObject {

	public Meeting() {
		this.enable = false;
		this.oauth2Id = "5";
		this.port = 5080;
		this.host = "127.0.0.1";
		this.user = "xadmin";
		this.pass = "";
		this.anonymousAccessAttachment = false;
	}

	public static Meeting defaultInstance() {
		return new Meeting();
	}

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("openMeeting单点序号")
	private String oauth2Id;
	@FieldDescribe("openMeeting端口")
	private Integer port;
	@FieldDescribe("openMeeting服务器")
	private String host;
	@FieldDescribe("openMeeting管理员账户")
	private String user;
	@FieldDescribe("openMeeting管理员密码")
	private String pass;
	@FieldDescribe("openMeeting协议")
	private String httpProtocol;
	@FieldDescribe("匿名用户是否可以访问附件")
	private Boolean anonymousAccessAttachment;

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

	public Boolean getAnonymousAccessAttachment() {
		return anonymousAccessAttachment;
	}

	public void setAnonymousAccessAttachment(Boolean anonymousAccessAttachment) {
		this.anonymousAccessAttachment = anonymousAccessAttachment;
	}

}
