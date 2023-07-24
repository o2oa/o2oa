package com.x.program.center.dingding;

import com.x.base.core.project.gson.GsonPropertyObject;

public class UserSimple extends GsonPropertyObject {

	private String userid;
	private String name;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}