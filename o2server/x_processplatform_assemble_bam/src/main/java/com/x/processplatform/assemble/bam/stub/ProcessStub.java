package com.x.processplatform.assemble.bam.stub;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ProcessStub extends GsonPropertyObject {

	private String name;
	private String value;
	private String applicationName;
	private String applicationCategory;
	private String applicationValue;

	private ActivityStubs activityStubs = new ActivityStubs();

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

	public ActivityStubs getActivityStubs() {
		return activityStubs;
	}

	public void setActivityStubs(ActivityStubs activityStubs) {
		this.activityStubs = activityStubs;
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

}
