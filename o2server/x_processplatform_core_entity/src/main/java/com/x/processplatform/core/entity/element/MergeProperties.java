package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class MergeProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("活动自定义数据")
	private JsonElement customData;

	@FieldDescribe("合并层级,0尽量多的合并,1合并1层,2合并不多于2层,3合并不多于3层.")
	private Integer mergeLayerThreshold;

	public Integer getMergeLayerThreshold() {
		return mergeLayerThreshold;
	}

	public void setMergeLayerThreshold(Integer mergeLayerThreshold) {
		this.mergeLayerThreshold = mergeLayerThreshold;
	}

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

}
