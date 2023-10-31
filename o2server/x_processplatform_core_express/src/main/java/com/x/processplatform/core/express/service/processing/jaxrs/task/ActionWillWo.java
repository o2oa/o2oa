package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class ActionWillWo extends GsonPropertyObject {

	private static final long serialVersionUID = 2279846765261247910L;

	private String defaultRouteName;

	private Boolean hasSole;

	private ActivityType nextActivityType;

	private String nextActivityName;

	private String nextActivityDescription;

	private String nextActivityAlias;

	private Boolean allowRapid;

	private List<String> nextTaskIdentityList = new ArrayList<>();

	public String getDefaultRouteName() {
		return defaultRouteName;
	}

	public void setDefaultRouteName(String defaultRouteName) {
		this.defaultRouteName = defaultRouteName;
	}

	public List<String> getNextTaskIdentityList() {
		return nextTaskIdentityList;
	}

	public void setNextTaskIdentityList(List<String> nextTaskIdentityList) {
		this.nextTaskIdentityList = nextTaskIdentityList;
	}

	public Boolean getAllowRapid() {
		return allowRapid;
	}

	public void setAllowRapid(Boolean allowRapid) {
		this.allowRapid = allowRapid;
	}

	public ActivityType getNextActivityType() {
		return nextActivityType;
	}

	public void setNextActivityType(ActivityType nextActivityType) {
		this.nextActivityType = nextActivityType;
	}

	public Boolean getHasSole() {
		return hasSole;
	}

	public void setHasSole(Boolean hasSole) {
		this.hasSole = hasSole;
	}

	public String getNextActivityName() {
		return nextActivityName;
	}

	public void setNextActivityName(String nextActivityName) {
		this.nextActivityName = nextActivityName;
	}

	public String getNextActivityDescription() {
		return nextActivityDescription;
	}

	public void setNextActivityDescription(String nextActivityDescription) {
		this.nextActivityDescription = nextActivityDescription;
	}

	public String getNextActivityAlias() {
		return nextActivityAlias;
	}

	public void setNextActivityAlias(String nextActivityAlias) {
		this.nextActivityAlias = nextActivityAlias;
	}

}