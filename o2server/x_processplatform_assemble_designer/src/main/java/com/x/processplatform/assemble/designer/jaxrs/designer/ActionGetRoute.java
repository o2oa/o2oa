package com.x.processplatform.assemble.designer.jaxrs.designer;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.Route;

class ActionGetRoute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetRoute.class);
	private final static String DESIGN_PROCESS_ROUTE = "route";

	ActionResult<Route> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Route> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Route route = emc.find(id, Route.class);
			result.setData(route);
		}
		return result;
	}

}
