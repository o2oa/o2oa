package com.x.server.console.action;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.server.console.Version;

public class WrapOutCheck extends GsonPropertyObject {

	private String current;
	private List<Version> followList;

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public List<Version> getFollowList() {
		return followList;
	}

	public void setFollowList(List<Version> followList) {
		this.followList = followList;
	}

}