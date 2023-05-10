package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class ManualProperties extends JsonProperties {

	private static final long serialVersionUID = -8141148907781411801L;
	@FieldDescribe("活动自定义数据")
	private JsonElement customData;

	@FieldDescribe("是否允许加签")
	private Boolean allowAddTask;

	@FieldDescribe("同一处理人不同身份待办合并处理一次.")
	private Boolean processingTaskOnceUnderSamePerson;

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

	public Boolean getAllowAddTask() {
		return allowAddTask;
	}

	public void setAllowAddTask(Boolean allowAddTask) {
		this.allowAddTask = allowAddTask;
	}

	public Boolean getProcessingTaskOnceUnderSamePerson() {
		return processingTaskOnceUnderSamePerson;
	}

	public void setProcessingTaskOnceUnderSamePerson(Boolean processingTaskOnceUnderSamePerson) {
		this.processingTaskOnceUnderSamePerson = processingTaskOnceUnderSamePerson;
	}

	public static class GoBack {
		
		private static final String TYPE_PREV = "prev";
		private String type;
		
		private String way;
		
		private Target
	}

}
