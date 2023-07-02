package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionOauthGetWo extends GsonPropertyObject {

	private static final long serialVersionUID = 4777997438663875200L;

	@FieldDescribe("名称.")
	@Schema(description = "名称.")
	private String name;

	@FieldDescribe("跳转地址.")
	@Schema(description = "跳转地址.")
	private String redirectUri;

	@FieldDescribe("认证地址.")
	@Schema(description = "认证地址.")
	private String authAddress;

	@FieldDescribe("认证方法类型.")
	@Schema(description = "认证方法类型.")
	private String authMethod;

	@FieldDescribe("认证参数.")
	@Schema(description = "认证参数.")
	private String authParameter;

	@FieldDescribe("图标.")
	@Schema(description = "图标.")
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public String getAuthParameter() {
		return authParameter;
	}

	public void setAuthParameter(String authParameter) {
		this.authParameter = authParameter;
	}

	public String getAuthAddress() {
		return authAddress;
	}

	public void setAuthAddress(String authAddress) {
		this.authAddress = authAddress;
	}
}