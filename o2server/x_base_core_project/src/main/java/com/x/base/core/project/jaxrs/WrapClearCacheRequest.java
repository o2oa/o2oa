package com.x.base.core.project.jaxrs;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class WrapClearCacheRequest extends GsonPropertyObject {

	@FieldDescribe("节点名")
	private String nodeName;

	@FieldDescribe("类名")
	private String className;

	@FieldDescribe("关键字")
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