package com.x.organization.core.express.person;

import java.util.List;

import com.x.base.core.project.AbstractContext;

class ActionListAll extends BaseAction {

	public static List<String> execute(AbstractContext context) throws Exception {
		Wo wo = context.applications().getQuery(applicationClass, "person/list/all").getData(Wo.class);
		return wo.getPersonList();
	}

	public static class Wo extends WoPersonListAbstract {
	}
}
