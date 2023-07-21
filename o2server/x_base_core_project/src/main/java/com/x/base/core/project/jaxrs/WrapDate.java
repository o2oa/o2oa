package com.x.base.core.project.jaxrs;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapDate extends GsonPropertyObject {

	public WrapDate() {
	}

	public WrapDate(Date date) throws Exception {
		this.date = date;
	}

	@FieldDescribe("date")
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
