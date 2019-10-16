package com.x.processplatform.core.express.query;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.query.CalculateEntry;

public class CalculateCell extends GsonPropertyObject {

	public CalculateCell() {

	}

	public CalculateCell(CalculateEntry calculateEntry, Object value) {
		this.column = calculateEntry.getColumn();
		this.displayName = calculateEntry.getDisplayName();
		this.value = value;
	}

	private String column;

	private String displayName;

	private Object value;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
