package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;

public class WorkCompletedProperties extends JsonProperties {

	private List<Record> recordList = new ArrayList<>();

	private Data data;

	public List<WorkLog> workLogList= new ArrayList<>();

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

	

}
