package com.x.processplatform.assemble.surface.wrapout.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.content.WorkLog;

public class WrapOutWorkLog extends WorkLog {

	private static final long serialVersionUID = 1307569946729101786L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long rank;

	private List<WrapOutTaskCompleted> taskCompletedList;

	private List<WrapOutTask> taskList;

	private Integer currentTaskIndex;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WrapOutTaskCompleted> getTaskCompletedList() {
		return taskCompletedList;
	}

	public void setTaskCompletedList(List<WrapOutTaskCompleted> taskCompletedList) {
		this.taskCompletedList = taskCompletedList;
	}

	public List<WrapOutTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WrapOutTask> taskList) {
		this.taskList = taskList;
	}

	public Integer getCurrentTaskIndex() {
		return currentTaskIndex;
	}

	public void setCurrentTaskIndex(Integer currentTaskIndex) {
		this.currentTaskIndex = currentTaskIndex;
	}

}
