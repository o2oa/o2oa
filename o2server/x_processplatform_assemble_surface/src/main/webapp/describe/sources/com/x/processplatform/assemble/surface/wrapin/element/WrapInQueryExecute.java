package com.x.processplatform.assemble.surface.wrapin.element;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.query.DateRangeEntry;

public class WrapInQueryExecute extends GsonPropertyObject {

	private DateRangeEntry date;

	private JsonElement filter;

	private JsonElement column;

	private JsonElement application;

	private JsonElement process;

	private JsonElement company;

	private JsonElement department;

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

	public JsonElement getProcess() {
		return process;
	}

	public void setProcess(JsonElement process) {
		this.process = process;
	}

	public JsonElement getCompany() {
		return company;
	}

	public void setCompany(JsonElement company) {
		this.company = company;
	}

	public JsonElement getDepartment() {
		return department;
	}

	public void setDepartment(JsonElement department) {
		this.department = department;
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
