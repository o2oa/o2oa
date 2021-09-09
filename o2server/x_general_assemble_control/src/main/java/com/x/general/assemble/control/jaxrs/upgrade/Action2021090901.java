package com.x.general.assemble.control.jaxrs.upgrade;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.core.entity.element.Route;

public class Action2021090901 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			List<Route> list = emc.listAll(Route.class);

			emc.beginTransaction(Route.class);

			for (Route route : list) {
				if ((null == route.getProperties().getDefaultSelected()) && (null != route.getSole())) {
					route.getProperties().setDefaultSelected(route.getSole());
				}
			}

			emc.commit();

			Wo wo = new Wo();

			wo.setValue(true);

			result.setData(wo);

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
