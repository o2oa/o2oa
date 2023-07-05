package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionReadableTypeCmsWi extends GsonPropertyObject {

	private static final long serialVersionUID = 302584944214794435L;

	private String person;

	private String doucment;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getDoucment() {
		return doucment;
	}

	public void setDoucment(String doucment) {
		this.doucment = doucment;
	}

}