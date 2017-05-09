package com.x.organization.core.express;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.project.Context;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapPersonAttribute;

public class PersonAttributeFactory {

	PersonAttributeFactory(Context context) {
		this.context = context;
	}

	private Context context;

	public WrapPersonAttribute getWithPerson(String name, String personName) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class, "personattribute/"
							+ URLEncoder.encode(name, "UTF-8") + "/person/" + URLEncoder.encode(personName, "UTF-8"))
					.getData(WrapPersonAttribute.class);
		} catch (Exception e) {
			throw new Exception("getWithPerson name:" + name + ", person:" + personName + "} error.", e);
		}
	}

	public List<WrapPersonAttribute> listWithPerson(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"personattribute/list/person/" + URLEncoder.encode(name, "UTF-8"))
					.getDataAsList(WrapPersonAttribute.class);
		} catch (Exception e) {
			throw new Exception("listWithPerson name:" + name + ", error.", e);
		}
	}

}
