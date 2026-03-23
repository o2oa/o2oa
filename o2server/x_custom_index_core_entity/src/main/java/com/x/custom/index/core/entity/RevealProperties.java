package com.x.custom.index.core.entity;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.express.index.Directory;

public class RevealProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("自定义数据.")
	private JsonElement data;

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

	public static final String PROCESSPLATFORMLIST_FIELDNAME = "processPlatformList";
	private List<Directory> processPlatformList;

	public static final String CMSLIST_FIELDNAME = "cmsList";
	private List<Directory> cmsList;

	public List<Directory> getProcessPlatformList() {
		return processPlatformList;
	}

	public void setProcessPlatformList(List<Directory> processPlatformList) {
		this.processPlatformList = processPlatformList;
	}

	public List<Directory> getCmsList() {
		return cmsList;
	}

	public void setCmsList(List<Directory> cmsList) {
		this.cmsList = cmsList;
	}

}
