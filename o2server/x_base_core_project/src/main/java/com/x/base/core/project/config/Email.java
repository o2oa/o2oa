package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.NumberTools;

public class Email extends ConfigObject {

	public static final Boolean DEFAULT_ENABLE = false;
	public static final String DEFAULT_HOST = "";
	public static final Integer DEFAULT_PORT = 25;
	public static final Integer DEFAULT_SSLPORT = 465;
	public static final Boolean DEFAULT_SSLENABLE = false;
	public static final String DEFAULT_FROM = "";
	public static final String DEFAULT_USER = "";
	public static final String DEFAULT_PASS = "";

	public static Email defaultInstance() {
		return new Email();
	}

	public Email() {
		this.enable = DEFAULT_ENABLE;
		this.host = DEFAULT_HOST;
		this.port = DEFAULT_PORT;
		this.sslEnable = DEFAULT_SSLENABLE;
		this.from = DEFAULT_FROM;
		this.user = DEFAULT_USER;
		this.pass = DEFAULT_PASS;
	}

	@FieldDescribe("是否启用")
	private Boolean enable;

	@FieldDescribe("SMTP主机地址")
	private String host;

	@FieldDescribe("SMTP发送端口")
	private Integer port;

	@FieldDescribe("是否启用ssl")
	private Boolean sslEnable;

	@FieldDescribe("发送人")
	private String from;

	@FieldDescribe("用户名")
	private String user;

	@FieldDescribe("密码")
	private String pass;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		if (NumberTools.nullOrLessThan(this.port, 1)) {
			if (BooleanUtils.isTrue(this.sslEnable)) {
				return DEFAULT_SSLPORT;
			} else {
				return DEFAULT_PORT;
			}
		} else {
			return this.port;
		}
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getFrom() {
		return from;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

}
