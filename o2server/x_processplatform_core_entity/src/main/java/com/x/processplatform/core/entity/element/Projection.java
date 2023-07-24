package com.x.processplatform.core.entity.element;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Projection extends GsonPropertyObject {

	private String path = "";
	private String type = "";
	private String name = "";
	private String scriptText = "";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}
}
