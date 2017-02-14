package com.x.common.core.application.definition;

import com.x.base.core.gson.GsonPropertyObject;

public class CollectDefinition extends LoadableDefinition {

	public static CollectDefinition INSTANCE;

	public static final String FILE_NAME = "collectDefinition.json";

	private String name;

	private String password;

	private Boolean enable;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}