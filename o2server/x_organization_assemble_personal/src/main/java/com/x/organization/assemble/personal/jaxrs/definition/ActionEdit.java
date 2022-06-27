package com.x.organization.assemble.personal.jaxrs.definition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Definition;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name, String wi) throws Exception {

		LOGGER.debug("execute:{}, name:{}.", effectivePerson::getDistinguishedName, () -> name);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Definition definition = emc.flag(name, Definition.class);
			emc.beginTransaction(Definition.class);
			if (null != definition) {
				definition.setData(wi);
				emc.check(definition, CheckPersistType.all);
			} else {
				definition = new Definition();
				definition.setData(wi);
				definition.setName(name);
				emc.persist(definition, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(Definition.class);
			Wo wo = new Wo();
			wo.setId(definition.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.definition.ActionEdit$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -2449779525503354868L;

	}
}
