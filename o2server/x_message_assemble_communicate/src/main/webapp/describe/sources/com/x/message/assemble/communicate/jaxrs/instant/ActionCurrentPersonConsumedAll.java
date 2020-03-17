package com.x.message.assemble.communicate.jaxrs.instant;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Instant;

class ActionCurrentPersonConsumedAll extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCurrentPersonConsumedAll.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			List<Instant> os = emc.listEqual(Instant.class, Instant.person_FIELDNAME,
					effectivePerson.getDistinguishedName());
			if (!os.isEmpty()) {
				emc.beginTransaction(Instant.class);
				for (Instant o : os) {
					o.setConsumed(true);
				}
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}