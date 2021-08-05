package com.x.organization.assemble.authentication.wrapin;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInAuthentication extends GsonPropertyObject {

	private static final long serialVersionUID = -808566215993013029L;

	@FieldDescribe("用户标识")
	private String credential;

	@FieldDescribe("密码")
	private String password;

	@FieldDescribe("认证码回复")
	private String codeAnswer;

	@FieldDescribe("验证码")
	private String captcha;

	@FieldDescribe("验证码回复")
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

	public String getCodeAnswer() {
		return codeAnswer;
	}

	public void setCodeAnswer(String codeAnswer) {
		this.codeAnswer = codeAnswer;
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
