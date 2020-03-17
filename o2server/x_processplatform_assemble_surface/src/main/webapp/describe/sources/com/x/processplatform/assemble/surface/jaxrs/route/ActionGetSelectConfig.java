package com.x.processplatform.assemble.surface.jaxrs.route;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Route;

class ActionGetSelectConfig extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetSelectConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug("id:{}.", id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Route route = business.route().pick(id);
			if (null == route) {
				throw new ExceptionEntityNotExist(id, Route.class);
			}
			Wo wo = new Wo();
			wo.setValue(route.getSelectConfig());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}

}
