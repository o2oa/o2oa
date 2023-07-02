package com.x.processplatform.assemble.surface.jaxrs.route;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Route;

class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug("jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			for (String id : wi.getValueList()) {
				Route route = business.route().pick(id);
				if (null != route) {
					wos.add(Wo.copier.copy(route));
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends WrapStringList {

	}

	public static class Wo extends Route {

		private static final long serialVersionUID = 5576391353458924290L;

		static WrapCopier<Route, Wo> copier = WrapCopierFactory.wo(Route.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}