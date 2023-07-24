package com.x.cms.core.entity;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Projection extends GsonPropertyObject {

	@FieldDescribe("数据路径")
	private String path = "";
	@FieldDescribe("映射字段类型")
	private String type = "";
	@FieldDescribe("数据名称")
	private String name = "";

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
}
