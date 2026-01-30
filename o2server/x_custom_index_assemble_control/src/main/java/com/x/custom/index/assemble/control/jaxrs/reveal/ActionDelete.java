package com.x.custom.index.assemble.control.jaxrs.reveal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Reveal reveal = emc.flag(flag, Reveal.class);
			if (null == reveal) {
				throw new ExceptionEntityNotExist(flag, Reveal.class);
			}
			this.checkEditDeleteAccess(effectivePerson, business, reveal);
			emc.beginTransaction(Reveal.class);
			emc.check(reveal, CheckRemoveType.all);
			emc.remove(reveal);
			emc.commit();
			CacheManager.notify(Reveal.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
		}
		return result;
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionDelete$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -3075483595711995360L;

	}

}
