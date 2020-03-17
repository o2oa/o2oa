package com.x.organization.assemble.personal.jaxrs.definition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Definition;

class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Definition o = emc.flag(name, Definition.class);
			Wo wo = new Wo();
			if (null != o) {
				emc.beginTransaction(Definition.class);
				emc.remove(o);
				emc.commit();
				wo.setId(o.getId());
				ApplicationCache.notify(Definition.class);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}
}
