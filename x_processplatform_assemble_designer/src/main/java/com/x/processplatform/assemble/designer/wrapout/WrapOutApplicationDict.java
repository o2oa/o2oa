package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.ApplicationDict;

@Wrap(ApplicationDict.class)
public class WrapOutApplicationDict extends ApplicationDict {

	private static final long serialVersionUID = -939143099553857829L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

}
