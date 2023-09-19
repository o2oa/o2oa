package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.accredit.Empower;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDisable extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDisable.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Empower empower = emc.find(id, Empower.class);
			if (null == empower) {
				throw new ExceptionEntityNotExist(id, Empower.class);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(empower.getFromPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, empower);
			}
			emc.beginTransaction(Empower.class);
			empower.setEnable(false);
			emc.check(empower, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Empower.class);
			Wo wo = new Wo();
			wo.setId(empower.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.empower.ActionDisable$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 5206233299613547177L;

	}

}
