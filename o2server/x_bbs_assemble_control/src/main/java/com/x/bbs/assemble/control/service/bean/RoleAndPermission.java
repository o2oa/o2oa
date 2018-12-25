package com.x.bbs.assemble.control.service.bean;

import java.util.List;

public class RoleAndPermission {
	
	private String person = null;
	
	private List<String> roleInfoList = null;
	
	private List<String> permissionInfoList = null;
	
	private List<String> visiableForumIds = null;
	
	private List<String> visiableSectionIds = null;
	
	private Boolean isBBSManager = false;

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

	public List<String> getVisibleForumIds() {
		return visiableForumIds;
	}

	public void setVisibleForumIds(List<String> visiableForumIds) {
		this.visiableForumIds = visiableForumIds;
	}

	public List<String> getVisibleSectionIds() {
		return visiableSectionIds;
	}

	public void setVisibleSectionIds(List<String> visiableSectionIds) {
		this.visiableSectionIds = visiableSectionIds;
	}

	public List<String> getVisiableForumIds() {
		return visiableForumIds;
	}

	public List<String> getVisiableSectionIds() {
		return visiableSectionIds;
	}

	public Boolean getIsBBSManager() {
		return isBBSManager;
	}

	public void setVisiableForumIds(List<String> visiableForumIds) {
		this.visiableForumIds = visiableForumIds;
	}

	public void setVisiableSectionIds(List<String> visiableSectionIds) {
		this.visiableSectionIds = visiableSectionIds;
	}

	public void setIsBBSManager(Boolean isBBSManager) {
		this.isBBSManager = isBBSManager;
	}
	
}
