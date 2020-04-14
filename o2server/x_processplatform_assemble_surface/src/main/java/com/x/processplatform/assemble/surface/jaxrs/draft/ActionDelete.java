package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Draft;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Draft draft = emc.find(id, Draft.class);
			if (null == draft) {
				throw new ExceptionEntityNotExist(id, Draft.class);
			}
			if ((!effectivePerson.isPerson(draft.getPerson())) && (!business
					.canManageApplicationOrProcess(effectivePerson, draft.getApplication(), draft.getProcess()))) {
				throw new ExceptionAccessDenied(effectivePerson, draft);
			}
			emc.beginTransaction(Draft.class);
			emc.remove(draft, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(draft.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
