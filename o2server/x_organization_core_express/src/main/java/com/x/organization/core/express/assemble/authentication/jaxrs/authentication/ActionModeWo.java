package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionModeWo extends GsonPropertyObject {

	private static final long serialVersionUID = -7141628080400713769L;

	@FieldDescribe("启用图片验证码登陆.")
	@Schema(description = "启用图片验证码登陆.")
	private Boolean captchaLogin = true;

	@FieldDescribe("启用短信验证码登陆.")
	@Schema(description = "启用短信验证码登陆.")
	private Boolean codeLogin = false;

	@FieldDescribe("扫描二维码登陆.")
	@Schema(description = "扫描二维码登陆.")
	private Boolean bindLogin = false;

	@FieldDescribe("人脸识别登陆.")
	@Schema(description = "人脸识别登陆.")
	private Boolean faceLogin = false;

	@FieldDescribe("双因素登陆.")
	@Schema(description = "双因素登陆.")
	private Boolean twoFactorLogin = false;

	public Boolean getCodeLogin() {
		return codeLogin;
	}

	public void setCodeLogin(Boolean codeLogin) {
		this.codeLogin = codeLogin;
	}

	public Boolean getBindLogin() {
		return bindLogin;
	}

	public void setBindLogin(Boolean bindLogin) {
		this.bindLogin = bindLogin;
	}

	public Boolean getFaceLogin() {
		return faceLogin;
	}

	public void setFaceLogin(Boolean faceLogin) {
		this.faceLogin = faceLogin;
	}

	public Boolean getCaptchaLogin() {
		return captchaLogin;
	}

	public void setCaptchaLogin(Boolean captchaLogin) {
		this.captchaLogin = captchaLogin;
	}

	public Boolean getTwoFactorLogin() {
		return twoFactorLogin;
	}

	public void setTwoFactorLogin(Boolean twoFactorLogin) {
		this.twoFactorLogin = twoFactorLogin;
	}
}
