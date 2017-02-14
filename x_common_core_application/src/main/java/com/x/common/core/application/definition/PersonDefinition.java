package com.x.common.core.application.definition;

public class PersonDefinition extends LoadableDefinition {

	public static PersonDefinition INSTANCE;

	public static final String FILE_NAME = "personDefinition.json";

	private String defaultPassword;
	private String defaultIconMale;
	private String defaultIconFemale;
	private String defaultIcon;

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
}
