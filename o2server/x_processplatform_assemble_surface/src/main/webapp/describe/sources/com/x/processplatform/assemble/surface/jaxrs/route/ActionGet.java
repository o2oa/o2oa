package com.x.processplatform.assemble.surface.jaxrs.route;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Route;

class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug("id:{}.", id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Route route = business.route().pick(id);
			if (null == route) {
				throw new ExceptionEntityNotExist(id, Route.class);
			}
			Wo wo = Wo.copier.copy(route);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Route {

		private static final long serialVersionUID = 5576391353458924290L;

		static WrapCopier<Route, Wo> copier = WrapCopierFactory.wo(Route.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
