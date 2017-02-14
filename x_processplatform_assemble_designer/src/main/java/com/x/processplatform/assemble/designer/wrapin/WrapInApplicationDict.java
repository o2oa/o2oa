package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.ApplicationDict;

@Wrap(ApplicationDict.class)
public class WrapInApplicationDict extends ApplicationDict {

	private static final long serialVersionUID = 7020926328082641485L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
