package com.x.query.core.entity.plan;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CalculateCell extends GsonPropertyObject {

	public CalculateCell() {

	}

	public CalculateCell(CalculateEntry calculateEntry, Object value) {
		this.column = calculateEntry.column;
		this.displayName = calculateEntry.displayName;
		this.value = value;
	}

	public String column;

	public String displayName;

	public Object value;

	public String getColumn() {
		return column;
	}

}