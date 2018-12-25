package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class ActivityStub extends GsonPropertyObject {

	private String name;
	private String value;
	private ActivityType activityType;
	private String applicationName;
	private String applicationCategory;
	private String applicationValue;
	private String processName;
	private String processValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationCategory() {
		return applicationCategory;
	}

	public void setApplicationCategory(String applicationCategory) {
		this.applicationCategory = applicationCategory;
	}

	public String getApplicationValue() {
		return applicationValue;
	}

	public void setApplicationValue(String applicationValue) {
		this.applicationValue = applicationValue;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessValue() {
		return processValue;
	}

	public void setProcessValue(String processValue) {
		this.processValue = processValue;
	}

}
