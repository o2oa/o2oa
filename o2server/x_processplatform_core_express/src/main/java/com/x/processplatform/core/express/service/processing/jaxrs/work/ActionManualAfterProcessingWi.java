package com.x.processplatform.core.express.service.processing.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;

public class ActionManualAfterProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -797953382184336448L;

	@FieldDescribe("待办")
	private Task task;
	@FieldDescribe("流转记录")
	private Record record;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

}