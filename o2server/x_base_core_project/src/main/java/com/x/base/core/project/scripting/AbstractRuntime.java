//package com.x.base.core.project.scripting;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.collections4.list.TreeList;
//
//import com.x.base.core.project.annotation.FieldDescribe;
//import com.x.base.core.project.gson.GsonPropertyObject;
//
//public abstract class AbstractRuntime extends GsonPropertyObject {
//
//	@FieldDescribe("当前用户")
//	private String person = "";
//
//	@FieldDescribe("组织")
//	private List<String> unitList = new TreeList<>();
//
//	@FieldDescribe("群组")
//	private List<String> groupList = new TreeList<>();
//
//	@FieldDescribe("角色")
//	private List<String> roleList = new TreeList<>();
//
//	@FieldDescribe("所有群组")
//	private List<String> unitAllList = new TreeList<>();
//
//	@FieldDescribe("身份")
//	private List<String> identityList = new TreeList<>();
//
//	@FieldDescribe("参数")
//	private Map<String, Object> parameter = new HashMap<>();
//
//	@FieldDescribe("数量")
//	private Integer count = 0;
//
//	public Map<String, Object> getParameter() {
//		if (this.parameter == null) {
//			this.parameter = new HashMap<String, Object>();
//		}
//		return this.parameter;
//	}
//
//	public String getPerson() {
//		return person;
//	}
//
//	public void setPerson(String person) {
//		this.person = person;
//	}
//
//	public List<String> getUnitList() {
//		return unitList;
//	}
//
//	public void setUnitList(List<String> unitList) {
//		this.unitList = unitList;
//	}
//
//	public List<String> getGroupList() {
//		return groupList;
//	}
//
//	public void setGroupList(List<String> groupList) {
//		this.groupList = groupList;
//	}
//
//	public List<String> getRoleList() {
//		return roleList;
//	}
//
//	public void setRoleList(List<String> roleList) {
//		this.roleList = roleList;
//	}
//
//	public List<String> getUnitAllList() {
//		return unitAllList;
//	}
//
//	public void setUnitAllList(List<String> unitAllList) {
//		this.unitAllList = unitAllList;
//	}
//
//	public List<String> getIdentityList() {
//		return identityList;
//	}
//
//	public void setIdentityList(List<String> identityList) {
//		this.identityList = identityList;
//	}
//
//	public Integer getCount() {
//		return count;
//	}
//
//	public void setCount(Integer count) {
//		this.count = count;
//	}
//
//	public void setParameter(Map<String, Object> parameter) {
//		this.parameter = parameter;
//	}
//
//
// 
//
//}
