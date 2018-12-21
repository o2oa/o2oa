package com.x.organization.assemble.authentication.wrapin;

import java.util.Date;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInLoginRecord extends GsonPropertyObject {

	private String name;

	private String address;
	
	private Date date;

	private String client;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}


}
