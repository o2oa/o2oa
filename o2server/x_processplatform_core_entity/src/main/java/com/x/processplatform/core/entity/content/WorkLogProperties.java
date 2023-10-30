package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.processplatform.core.entity.element.ActivityType;

public class WorkLogProperties extends JsonProperties {

	private static final long serialVersionUID = -4149341123576111783L;

	private List<String> splitTokenList;

	private List<String> splitValueList = new ArrayList<>();

	private ActivityType goBackFromActivityType;

	private String goBackFromActivity;

	private String goBackFromActivityToken;

	public ActivityType getGoBackFromActivityType() {
		return goBackFromActivityType;
	}

	public void setGoBackFromActivityType(ActivityType goBackFromActivityType) {
		this.goBackFromActivityType = goBackFromActivityType;
	}

	public String getGoBackFromActivity() {
		return goBackFromActivity;
	}

	public void setGoBackFromActivity(String goBackFromActivity) {
		this.goBackFromActivity = goBackFromActivity;
	}

	public String getGoBackFromActivityToken() {
		return goBackFromActivityToken;
	}

	public void setGoBackFromActivityToken(String goBackFromActivityToken) {
		this.goBackFromActivityToken = goBackFromActivityToken;
	}

	public List<String> getSplitTokenList() {
		if (null == splitTokenList) {
			this.splitTokenList = new ArrayList<>();
		}
		return splitTokenList;
	}

	public List<String> getSplitValueList() {
		if (null == splitValueList) {
			this.splitValueList = new ArrayList<String>();
		}
		return splitValueList;
	}

	public void setSplitTokenList(List<String> splitTokenList) {
		this.splitTokenList = splitTokenList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

}
