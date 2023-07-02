package com.x.organization.core.express.person;

import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Person;

class ActionListAllObject extends BaseAction {

	public static List<Wo> execute(AbstractContext context) throws Exception {
		List<Wo> wos = context.applications().getQuery(applicationClass, "person/list/all/object")
				.getDataAsList(Wo.class);
		return wos;
	}

	public static class Wo extends Person {

	}
}
