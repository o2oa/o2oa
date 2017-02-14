package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapPersonAttribute;

public class PersonAttributeFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapPersonAttribute>>() {
	}.getType();

	public WrapPersonAttribute getWithPerson(String name, String personName) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class, "personattribute/"
					+ URLEncoder.encode(name, "UTF-8") + "/person/" + URLEncoder.encode(personName, "UTF-8"),
					WrapPersonAttribute.class);
		} catch (Exception e) {
			throw new Exception("getWithPerson name:" + name + ", person:" + personName + "} error.", e);
		}
	}

	public List<WrapPersonAttribute> listWithPerson(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"personattribute/list/person/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPerson name:" + name + ", error.", e);
		}
	}

}
