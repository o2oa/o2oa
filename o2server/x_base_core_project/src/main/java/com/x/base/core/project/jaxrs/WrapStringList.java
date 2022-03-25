package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapStringList extends GsonPropertyObject {

	public WrapStringList() {
	}

	public WrapStringList(Collection<String> collection) {
		this.valueList = new ArrayList<String>(collection);
	}

	@FieldDescribe("字符串多值.")
	private List<String> valueList = new ArrayList<>();

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	public List<String> addValue(String value, Boolean unique) {
		if (this.valueList == null) {
			this.valueList = new ArrayList<>();
		}
		if (BooleanUtils.isTrue(unique)) {
			if (!this.valueList.contains(value)) {
				this.valueList.add(value);
			}
		} else {
			this.valueList.add(value);
		}
		return this.valueList;
	}

}