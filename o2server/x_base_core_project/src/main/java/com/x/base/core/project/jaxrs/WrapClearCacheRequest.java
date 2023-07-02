package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class WrapClearCacheRequest extends GsonPropertyObject {

	public static final String TYPE_NOTIFY = "notify";
	public static final String TYPE_RECEIVE = "receive";

	private static final long serialVersionUID = -4221561865395746387L;

	private String type;

	@FieldDescribe("节点名.")
	@Schema(description = "节点名.")
	private String nodeName;

	@FieldDescribe("类名.")
	@Schema(description = "类名.")
	private String className;

	@FieldDescribe("关键字.")
	@Schema(description = "关键字.")
	private List<Object> keys;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<Object> getKeys() {
		return null == keys ? new ArrayList<>() : keys;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}