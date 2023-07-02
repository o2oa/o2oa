package com.x.processplatform.core.express.assemble.surface.jaxrs.draft;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionSaveWi extends GsonPropertyObject {

	private static final long serialVersionUID = 447720452199696830L;

	@FieldDescribe("业务数据.")
	@Schema(description = "业务数据.")
	private Data data = new Data();

	@FieldDescribe("工作.")
	@Schema(description = "工作.")
	private Work work;

	@FieldDescribe("身份.")
	@Schema(description = "身份.")
	private String identity;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}