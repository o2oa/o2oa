package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDeleteAll extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteAll.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Component.class);
			for (Component o : emc.listAll(Component.class)) {
				emc.remove(o, CheckRemoveType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			CacheManager.notify(Component.class);
			return result;
		}
	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionDeleteAll$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 4441944401153239222L;

	}

}
