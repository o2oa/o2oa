package com.x.base.core.project.http;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInStringList extends GsonPropertyObject {

	public WrapInStringList() {
		this.valueList = new ArrayList<String>();
	}

	private List<String> valueList;

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

}
