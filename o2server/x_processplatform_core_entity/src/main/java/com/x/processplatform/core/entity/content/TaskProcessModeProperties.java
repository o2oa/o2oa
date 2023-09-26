package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class TaskProcessModeProperties extends JsonProperties {

	private static final long serialVersionUID = -6568608317372916655L;

	private List<TaskProcessModeItem> taskProcessModeItemList = new ArrayList<>();

	public List<TaskProcessModeItem> getTaskProcessModeItemList() {
		return taskProcessModeItemList;
	}

	public void setTaskProcessModeItemList(List<TaskProcessModeItem> taskProcessModeItemList) {
		this.taskProcessModeItemList = taskProcessModeItemList;
	}
}
