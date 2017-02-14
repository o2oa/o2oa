package com.x.processplatform.assemble.surface.wrapin.content;

import com.google.gson.JsonElement;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.content.Work;

@Wrap(Work.class)
public class WrapInWork extends GsonPropertyObject {

	private String title;

	private String identity;

	private JsonElement data;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
