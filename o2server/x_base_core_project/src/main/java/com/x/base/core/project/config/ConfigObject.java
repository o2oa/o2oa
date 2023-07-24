package com.x.base.core.project.config;

import java.util.LinkedHashMap;
import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ConfigObject extends GsonPropertyObject {

	private static final long serialVersionUID = 5563957679735192940L;
	@FieldDescribe("扩展设置.")
	private Map<String, Object> extension;

	public void extension(Map<String, Object> extension) {
		this.extension = extension;
	}

	public Map<String, Object> extension() {
		return null == this.extension ? new LinkedHashMap<>() : this.extension;
	}

}
