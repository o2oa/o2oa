package com.x.base.core.project.organization;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class UnitDuty extends GsonPropertyObject {

	@FieldDescribe("个人属性名称")
	private String name;

	@FieldDescribe("组织")
	private String unit;

	@FieldDescribe("身份对象")
	private List<Identity> identityList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<Identity> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<Identity> identityList) {
		this.identityList = identityList;
	}

}
