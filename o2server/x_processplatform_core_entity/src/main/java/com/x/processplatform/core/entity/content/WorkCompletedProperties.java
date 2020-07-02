package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class WorkCompletedProperties extends JsonProperties {

	@FieldDescribe("合并记录对象")
	private List<Record> recordList = new ArrayList<>();

	@FieldDescribe("合并数据对象")
	private Data data;

	@FieldDescribe("合并工作日志对象")
	public List<WorkLog> workLogList = new ArrayList<>();

	@FieldDescribe("标题")
	private String title;

	public List<Record> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<Record> recordList) {
		this.recordList = recordList;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<WorkLog> getWorkLogList() {
		return workLogList;
	}

	public void setWorkLogList(List<WorkLog> workLogList) {
		this.workLogList = workLogList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
