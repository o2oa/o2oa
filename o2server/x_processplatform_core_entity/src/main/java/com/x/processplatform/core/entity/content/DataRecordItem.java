package com.x.processplatform.core.entity.content;

import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.Date;

/**
 * @author sword
 */
public class DataRecordItem extends GsonPropertyObject {

	private static final long serialVersionUID = 7549849471793331587L;

	public DataRecordItem(){
		this.updateDate = new Date();
	}

	public DataRecordItem(String person, String activity, String activityName){
		this.person = person;
		this.activity = activity;
		this.activityName = activityName;
		this.updateDate = new Date();
	}

	private String person;

	private Date updateDate;

	private String activity;

	private String activityName;

	private String newData;

	private String oldData;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getNewData() {
		return newData;
	}

	public void setNewData(String newData) {
		this.newData = newData;
	}

	public String getOldData() {
		return oldData;
	}

	public void setOldData(String oldData) {
		this.oldData = oldData;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
