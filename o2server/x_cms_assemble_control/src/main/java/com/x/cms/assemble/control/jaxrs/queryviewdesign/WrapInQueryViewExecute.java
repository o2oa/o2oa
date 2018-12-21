package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.cms.core.entity.query.DateRangeEntry;

public class WrapInQueryViewExecute extends GsonPropertyObject {

	private DateRangeEntry date;

	private JsonElement filter;

	private JsonElement column;

	private JsonElement application;

	private JsonElement category;

	private JsonElement unit;

	private JsonElement person;

	private JsonElement identity;

	public DateRangeEntry getDate() {
		return date;
	}

	public void setDate(DateRangeEntry date) {
		this.date = date;
	}

	public JsonElement getFilter() {
		return filter;
	}

	public void setFilter(JsonElement filter) {
		this.filter = filter;
	}

	public JsonElement getColumn() {
		return column;
	}

	public void setColumn(JsonElement column) {
		this.column = column;
	}

	public JsonElement getApplication() {
		return application;
	}

	public void setApplication(JsonElement application) {
		this.application = application;
	}	

	public JsonElement getCategory() {
		return category;
	}

	public void setCategory(JsonElement category) {
		this.category = category;
	}

	public JsonElement getUnit() {
		return unit;
	}

	public void setUnit(JsonElement unit) {
		this.unit = unit;
	}

	public JsonElement getPerson() {
		return person;
	}

	public void setPerson(JsonElement person) {
		this.person = person;
	}

	public JsonElement getIdentity() {
		return identity;
	}

	public void setIdentity(JsonElement identity) {
		this.identity = identity;
	}

}