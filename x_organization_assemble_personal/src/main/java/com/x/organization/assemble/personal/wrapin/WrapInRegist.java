package com.x.organization.assemble.personal.wrapin;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Person;

@Wrap(Person.class)
public class WrapInRegist extends GsonPropertyObject {

	private String name;

	private GenderType genderType;

	private String password;

	private String mobile;

	private String codeAnswer;

	private String captchaAnswer;

	private String captcha;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCodeAnswer() {
		return codeAnswer;
	}

	public void setCodeAnswer(String codeAnswer) {
		this.codeAnswer = codeAnswer;
	}

	public String getCaptchaAnswer() {
		return captchaAnswer;
	}

	public void setCaptchaAnswer(String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

}
