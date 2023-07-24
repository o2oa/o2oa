package com.x.cms.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class FileInfoProperties extends JsonProperties {

	private static final long serialVersionUID = 2260565650639079889L;

	@FieldDescribe("名称")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
