package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class TaskCompletedProperties extends JsonProperties {

	@FieldDescribe("从task带过来的上一处理人")
	private List<String> prevTaskIdentityList;

	@FieldDescribe("后续处理人")
	private List<String> nextTaskIdentityList = new ArrayList<>();

	@FieldDescribe("标题")
	private String title;

	@FieldDescribe("意见")
	private String opinion;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getPrevTaskIdentityList() {
		if (null == this.prevTaskIdentityList) {
			this.prevTaskIdentityList = new ArrayList<>();
		}
		return prevTaskIdentityList;
	}

	public List<String> getNextTaskIdentityList() {
		if (null == this.nextTaskIdentityList) {
			this.nextTaskIdentityList = new ArrayList<>();
		}
		return nextTaskIdentityList;
	}

	public void setNextTaskIdentityList(List<String> nextTaskIdentityList) {
		this.nextTaskIdentityList = nextTaskIdentityList;
	}

	public void setPrevTaskIdentityList(List<String> prevTaskIdentityList) {
		this.prevTaskIdentityList = prevTaskIdentityList;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}
