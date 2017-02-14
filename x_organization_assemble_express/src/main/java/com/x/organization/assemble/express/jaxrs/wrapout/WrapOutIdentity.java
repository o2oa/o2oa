package com.x.organization.assemble.express.jaxrs.wrapout;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Identity;

@Wrap(Identity.class)
public class WrapOutIdentity extends GsonPropertyObject {

	private String name;
	private String person;
	private String display;
	private String department;

	public String getName() {
		return name;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	

}