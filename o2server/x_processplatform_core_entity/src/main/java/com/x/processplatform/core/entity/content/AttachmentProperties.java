package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttachmentProperties extends JsonProperties {

	private static final long serialVersionUID = 2981030575785426818L;

	@FieldDescribe("标题.")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
