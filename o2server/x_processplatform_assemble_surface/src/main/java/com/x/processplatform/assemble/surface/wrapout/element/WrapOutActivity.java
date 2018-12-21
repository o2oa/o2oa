package com.x.processplatform.assemble.surface.wrapout.element;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class WrapOutActivity extends GsonPropertyObject {

	private String id;

	private String name;

	private String description;

	private String alias;

	private String position;

	private ActivityType activityType;

	private String resetRange;

	private Integer resetCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getResetCount() {
		return resetCount;
	}

	public void setResetCount(Integer resetCount) {
		this.resetCount = resetCount;
	}

	public String getResetRange() {
		return resetRange;
	}

	public void setResetRange(String resetRange) {
		this.resetRange = resetRange;
	}

}