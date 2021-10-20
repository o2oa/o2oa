package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.ApplicationDict;

public class WrapOutAppDict extends ApplicationDict {

	private static final long serialVersionUID = 49779311317884168L;
	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
