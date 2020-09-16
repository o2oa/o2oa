package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WoText  extends GsonPropertyObject {

	public WoText() {
	}

	public WoText(String text) throws Exception {
		this.text = text;
	}

	@FieldDescribe("text")
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
