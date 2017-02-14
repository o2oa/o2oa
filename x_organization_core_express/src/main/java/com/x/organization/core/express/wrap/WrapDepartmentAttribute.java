package com.x.organization.core.express.wrap;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapDepartmentAttribute extends GsonPropertyObject {

	private String id;
	
	private String pinyin;
	private String pinyinInitial;
	private String name;
	private String unique;
	private String department;
	private List<String> attributeList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getPinyinInitial() {
		return pinyinInitial;
	}
	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public List<String> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}
	public String getUnique() {
		return unique;
	}
	public void setUnique(String unique) {
		this.unique = unique;
	}
}
