package com.x.base.core.project.organization;

import com.x.base.core.project.gson.GsonPropertyObject;

public class IdentityPersonPair extends GsonPropertyObject {

	private static final long serialVersionUID = -3483694024750573996L;
	private String identity;
	private String person;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
