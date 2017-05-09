package com.x.organization.assemble.personal.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Person;

@Wrap(Person.class)
public class WrapOutPerson extends Person {

	private static final long serialVersionUID = -6312209377479051454L;
	public static List<String> Excludes = new ArrayList<>();

	private List<Map<String, Object>> identityList = new ArrayList<>();

	private String onlineStatus;

	static {
		Excludes.add(JpaObject.DISTRIBUTEFACTOR);
		Excludes.add("password");
	}

	public List<Map<String, Object>> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<Map<String, Object>> identityList) {
		this.identityList = identityList;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

}
