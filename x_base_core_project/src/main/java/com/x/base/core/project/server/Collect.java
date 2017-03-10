package com.x.base.core.project.server;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.gson.GsonPropertyObject;

public class Collect extends GsonPropertyObject {

	public static Collect defaultInstance() {
		return new Collect();
	}

	public Collect() {
		this.enable = false;
		this.name = "";
		this.password = "";
	}

	private Boolean enable;
	private String name;
	private String password;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getName() {
		return Objects.toString(this.name, "");
	}

	public String getPassword() {
		return Objects.toString(this.password, "");
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}