package com.x.bbs.assemble.control.service.bean;

import java.util.List;

public class RoleAndPermission {
	
	private String person = null;
	
	private List<String> roleInfoList = null;
	
	private List<String> permissionInfoList = null;
	
	private List<String> visiableForumIds = null;
	
	private List<String> visiableSectionIds = null;

	public List<String> getRoleInfoList() {
		return roleInfoList;
	}

	public void setRoleInfoList(List<String> roleInfoList) {
		this.roleInfoList = roleInfoList;
	}

	public List<String> getPermissionInfoList() {
		return permissionInfoList;
	}

	public void setPermissionInfoList(List<String> permissionInfoList) {
		this.permissionInfoList = permissionInfoList;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public List<String> getVisiableForumIds() {
		return visiableForumIds;
	}

	public void setVisiableForumIds(List<String> visiableForumIds) {
		this.visiableForumIds = visiableForumIds;
	}

	public List<String> getVisiableSectionIds() {
		return visiableSectionIds;
	}

	public void setVisiableSectionIds(List<String> visiableSectionIds) {
		this.visiableSectionIds = visiableSectionIds;
	}
	
}
