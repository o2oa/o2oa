package com.x.processplatform.assemble.surface.wrapin.element;

import com.google.gson.JsonElement;
import com.x.processplatform.core.entity.element.ApplicationDict;

public class WrapInApplicationDict extends ApplicationDict {

	private static final long serialVersionUID = 6419951244780354684L;
	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}
}
