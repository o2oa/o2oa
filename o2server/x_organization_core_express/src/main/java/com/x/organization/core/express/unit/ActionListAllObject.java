package com.x.organization.core.express.unit;

import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Unit;

class ActionListAllObject extends BaseAction {

	public static List<Wo> execute(AbstractContext context) throws Exception {
		List<Wo> wos = context.applications().getQuery(applicationClass, "unit/list/all/object")
				.getDataAsList(Wo.class);
		return wos;
	}

	public static class Wo extends Unit {

	}
}
