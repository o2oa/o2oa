package com.x.organization.core.express.unit;

import java.util.List;

import com.x.base.core.project.AbstractContext;

class ActionListAll extends BaseAction {

	public static List<String> execute(AbstractContext context) throws Exception {
		Wo wo = context.applications().getQuery(applicationClass, "unit/list/all").getData(Wo.class);
		return wo.getUnitList();
	}


	public static class Wo extends WoUnitListAbstract {
	}
}
