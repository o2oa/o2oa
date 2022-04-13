package com.x.message.assemble.communicate.jaxrs.org;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Org;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			ActionResult<Wo> result = new ActionResult<>();
			emc.beginTransaction(Org.class);
			Org org = emc.find(id, Org.class);

			if (null == org) {
				throw new ExceptionEntityNotExist(id, Org.class);
			}

			emc.remove(org, CheckRemoveType.all);
			emc.commit();

			Wo wo = new Wo();
			wo.setId(org.getId());
			result.setData(wo);
			return result;

		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 3088386806977478469L;

	}

}
