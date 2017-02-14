package com.x.base.core.cache;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;

public class ClearCacheRequest extends GsonPropertyObject {

	private String nodeName;

	private String className;

	private List<Object> keys;

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

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

}
