package com.x.processplatform.core.entity.element;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ManualProperties extends JsonProperties {

	private static final long serialVersionUID = -8141148907781411801L;
	@FieldDescribe("活动自定义数据")
	private JsonElement customData;

	@FieldDescribe("是否允许加签")
	private Boolean allowAddTask;

	@FieldDescribe("同一处理人不同身份待办合并处理一次.")
	private Boolean processingTaskOnceUnderSamePerson;

	@FieldDescribe("回退配置.")
	private GoBackConfig goBackConfig;

	public GoBackConfig getGoBackConfig() {
		return goBackConfig;
	}

	public void setGoBackConfig(GoBackConfig goBackConfig) {
		this.goBackConfig = goBackConfig;
	}

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

	public static class GoBackConfig extends GsonPropertyObject {

		private static final long serialVersionUID = -2057532080381874273L;

		public static final String TYPE_PREV = "prev";
		public static final String TYPE_ANY = "any";
		public static final String TYPE_DEFINE = "define";

		public static final String WAY_STEP = "step";
		public static final String WAY_JUMP = "jump";
		public static final String WAY_CUSTOM = "custom";

		private String type;

		private String way;

		private List<DefineConfig> defineConfigList = new ArrayList<>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

		public List<DefineConfig> getDefineConfigList() {
			return defineConfigList;
		}

		public void setDefineConfigList(List<DefineConfig> defineConfigList) {
			this.defineConfigList = defineConfigList;
		}

	}

	public static class DefineConfig extends GsonPropertyObject {

		private static final long serialVersionUID = 8022864967621176462L;

		private String activity;

		private String way;

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

	}

}
