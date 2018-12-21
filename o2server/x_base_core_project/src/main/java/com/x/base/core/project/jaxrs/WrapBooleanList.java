package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public  class WrapBooleanList extends GsonPropertyObject {

	public WrapBooleanList() {
	}

	public WrapBooleanList(Collection<Boolean> collection) {
		this.valueList = new ArrayList<Boolean>(collection);
	}

	@FieldDescribe("布尔值多值.")
	private List<Boolean> valueList = new ArrayList<>();

	public List<Boolean> getValueList() {
		return valueList;
	}

	public void setValueList(List<Boolean> valueList) {
		this.valueList = valueList;
	}

}