package com.x.common.core.application.definition;

import com.x.base.core.gson.GsonPropertyObject;

public class SsoDefinition extends LoadableDefinition {

	public static SsoDefinition INSTANCE;

	public static final String FILE_NAME = "ssoDefinition.json";

	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}