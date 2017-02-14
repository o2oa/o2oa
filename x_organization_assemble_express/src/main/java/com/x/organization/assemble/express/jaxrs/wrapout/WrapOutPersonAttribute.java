package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.PersonAttribute;


@Wrap(PersonAttribute.class)
public class WrapOutPersonAttribute extends GsonPropertyObject {

	private String name;
	private List<String> attributeList;
	private String person;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}

	
}