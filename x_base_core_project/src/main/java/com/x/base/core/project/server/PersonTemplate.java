package com.x.base.core.project.server;

import com.x.base.core.gson.GsonPropertyObject;

public class PersonTemplate extends GsonPropertyObject {

	public static final String RegularExpression_Script = "^\\((.+?)\\)$";

	private String defaultPassword;
	private String defaultIconMale;
	private String defaultIconFemale;
	private String defaultIcon;
	private Integer defaultPasswordPeriod;

	public String getDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public String getDefaultIconMale() {
		return defaultIconMale;
	}

	public void setDefaultIconMale(String defaultIconMale) {
		this.defaultIconMale = defaultIconMale;
	}

	public String getDefaultIconFemale() {
		return defaultIconFemale;
	}

	public void setDefaultIconFemale(String defaultIconFemale) {
		this.defaultIconFemale = defaultIconFemale;
	}

	public String getDefaultIcon() {
		return defaultIcon;
	}

	public void setDefaultIcon(String defaultIcon) {
		this.defaultIcon = defaultIcon;
	}

	public Integer getDefaultPasswordPeriod() {
		return defaultPasswordPeriod;
	}

	public void setDefaultPasswordPeriod(Integer defaultPasswordPeriod) {
		this.defaultPasswordPeriod = defaultPasswordPeriod;
	}

}
