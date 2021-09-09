package com.x.general.assemble.control.jaxrs.update;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.core.entity.element.Route;

public class Action2021090902 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			List<Route> list = emc.listAll(Route.class);

			emc.beginTransaction(Route.class);

			for (Route route : list) {
				if (null != route.getProperties().getSoleDirect()) {
					route.getProperties().setSoleDirect(false);
				}
			}

			emc.commit();

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
