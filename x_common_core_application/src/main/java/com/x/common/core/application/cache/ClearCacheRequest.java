package com.x.common.core.application.cache;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.common.core.application.Config;

public class ClearCacheRequest extends GsonPropertyObject {

	private Config config;

	private String className;

	private List<Object> keys;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<Object> getKeys() {
		return keys;
	}

	public void setKeys(List<Object> keys) {
		this.keys = keys;
	}

}
