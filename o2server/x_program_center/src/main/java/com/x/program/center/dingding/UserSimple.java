package com.x.program.center.dingding;

import com.x.base.core.project.gson.GsonPropertyObject;

public class UserSimple extends GsonPropertyObject {

	private static final long serialVersionUID = -5028505482006655529L;
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