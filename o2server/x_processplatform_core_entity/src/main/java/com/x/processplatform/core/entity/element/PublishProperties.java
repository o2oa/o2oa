package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

/**
 * @author sword
 */
public class PublishProperties extends JsonProperties {

	private static final long serialVersionUID = -4838100653273876451L;

	@FieldDescribe("活动自定义数据")
	private JsonElement customData;

	@FieldDescribe("发布的数据表")
	private List<PublishTable> publishTableList;

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

	public List<PublishTable> getPublishTableList() {
		return publishTableList;
	}

	public void setPublishTableList(List<PublishTable> publishTableList) {
		this.publishTableList = publishTableList;
	}
}
