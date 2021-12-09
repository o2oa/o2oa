package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class CancelProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("活动自定义数据")
	private JsonElement customData;

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

}
