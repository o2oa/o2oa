package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;

public class WrapInFilter{
	
	public static List<String> Excludes = new ArrayList<String>();
	
	private String organizationName = null;
	
	private String userName = null;
	
	private String forumId = null;
	
	private String sectionId = null;
	
	private String bindRoleCode = null;
	
	private BindObject bindObject = null;
	
	private List<String> bindRoleCodes = null;
	
	private List<BindObject> bindObjectArray = null;

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public List<String> getBindRoleCodes() {
		return bindRoleCodes;
	}

	public void setBindRoleCodes(List<String> bindRoleCodes) {
		this.bindRoleCodes = bindRoleCodes;
	}

	public List<BindObject> getBindObjectArray() {
		return bindObjectArray;
	}

	public void setBindObjectArray(List<BindObject> bindObjectArray) {
		this.bindObjectArray = bindObjectArray;
	}

	public String getBindRoleCode() {
		return bindRoleCode;
	}

	public void setBindRoleCode(String bindRoleCode) {
		this.bindRoleCode = bindRoleCode;
	}

	public BindObject getBindObject() {
		return bindObject;
	}

	public void setBindObject(BindObject bindObject) {
		this.bindObject = bindObject;
	}
}
