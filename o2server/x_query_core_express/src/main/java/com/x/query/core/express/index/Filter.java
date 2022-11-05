package com.x.query.core.express.index;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.Filter")
public class Filter extends GsonPropertyObject {

	private static final long serialVersionUID = 8404002412785606829L;

	String field;

	List<String> valueList;

	String start;

	String end;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

}
