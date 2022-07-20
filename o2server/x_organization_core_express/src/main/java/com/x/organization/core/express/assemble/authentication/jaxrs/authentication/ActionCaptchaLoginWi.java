package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaLoginWi extends GsonPropertyObject {

	private static final long serialVersionUID = 216758837350255868L;

	@FieldDescribe("用户凭证.")
	@Schema(description = "用户凭证.")
	private String credential;

	@FieldDescribe("密码.")
	@Schema(description = "密码.")
	private String password;

	@FieldDescribe("图片认证码编号.")
	@Schema(description = "图片认证码编号.")
	private String captcha;

	@FieldDescribe("图片认证码.")
	@Schema(description = "图片认证码.")
	private String captchaAnswer;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getCaptchaAnswer() {
		return captchaAnswer;
	}

	public void setCaptchaAnswer(String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}

}