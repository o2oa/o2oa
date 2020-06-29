package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class WorkProperties extends JsonProperties {

	@FieldDescribe("强制待办处理人")
	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	@FieldDescribe("授权对象")
	private LinkedHashMap<String, String> manualEmpowerMap = new LinkedHashMap<>();

	@FieldDescribe("服务回调值")
	private LinkedHashMap<String, Object> serviceValue = new LinkedHashMap<>();

	@FieldDescribe("标题")
	private String title;

	public List<String> getManualForceTaskIdentityList() {
		if (this.manualForceTaskIdentityList == null) {
			this.manualForceTaskIdentityList = new ArrayList<String>();
		}
		return this.manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

	public LinkedHashMap<String, String> getManualEmpowerMap() {
		if (this.manualEmpowerMap == null) {
			this.manualEmpowerMap = new LinkedHashMap<String, String>();
		}
		return this.manualEmpowerMap;
	}

	public void setManualEmpowerMap(LinkedHashMap<String, String> manualEmpowerMap) {
		this.manualEmpowerMap = manualEmpowerMap;
	}

	public LinkedHashMap<String, Object> getServiceValue() {
		if (this.serviceValue == null) {
			this.serviceValue = new LinkedHashMap<String, Object>();
		}
		return this.serviceValue;
	}

	public void setServiceValue(LinkedHashMap<String, Object> serviceValue) {
		this.serviceValue = serviceValue;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
