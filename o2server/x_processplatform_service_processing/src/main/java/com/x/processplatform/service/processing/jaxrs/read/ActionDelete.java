package com.x.processplatform.service.processing.jaxrs.read;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.service.processing.MessageFactory;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionReadNotExist(id);
			}
			emc.beginTransaction(Read.class);
			emc.remove(read, CheckRemoveType.all);
			emc.commit();
			MessageFactory.read_delete(read);
			Wo wo = new Wo();
			wo.setId(read.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

}
