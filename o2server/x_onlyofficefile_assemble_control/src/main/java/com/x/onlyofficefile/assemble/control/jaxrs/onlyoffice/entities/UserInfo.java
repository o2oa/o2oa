package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities;

/**
 * @author sword
 */
public class UserInfo {

	private String id = "";
	private String name = "";
	private String permission = "read";
	private String avatarUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
