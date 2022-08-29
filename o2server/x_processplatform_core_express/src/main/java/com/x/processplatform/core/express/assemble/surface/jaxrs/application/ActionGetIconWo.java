package com.x.processplatform.core.express.assemble.surface.jaxrs.application;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionGetIconWo extends GsonPropertyObject {

	private static final long serialVersionUID = 2554440356814790897L;

	@FieldDescribe("应用图标base64编码值.")
	@Schema(description = "应用图标base64编码值.")
	private String icon;

	@FieldDescribe("应用图标色调.")
	@Schema(description = "应用图标色调.")
	private String iconHue;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconHue() {
		return iconHue;
	}

	public void setIconHue(String iconHue) {
		this.iconHue = iconHue;
	}

}