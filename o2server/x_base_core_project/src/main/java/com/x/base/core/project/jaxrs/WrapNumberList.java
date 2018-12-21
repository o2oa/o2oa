package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public   class WrapNumberList extends GsonPropertyObject {

	public WrapNumberList() {
	}

	public WrapNumberList(Collection<Number> collection) {
		this.valueList = new ArrayList<Number>(collection);
	}

	@FieldDescribe("数值多值.")
	private List<Number> valueList = new ArrayList<>();

	public List<Number> getValueList() {
		return valueList;
	}

	public void setValueList(List<Number> valueList) {
		this.valueList = valueList;
	}

}