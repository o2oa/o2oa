package com.x.cms.assemble.control.jaxrs.appdict;

import com.google.gson.JsonElement;
import com.x.cms.core.entity.element.AppDict;

public class WrapInAppDict extends AppDict {

	private static final long serialVersionUID = 6419951244780354684L;
	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}
}
