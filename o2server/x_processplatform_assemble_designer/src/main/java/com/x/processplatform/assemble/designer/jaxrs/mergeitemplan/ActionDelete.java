package com.x.processplatform.assemble.designer.jaxrs.mergeitemplan;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.log.MergeItemPlan;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			MergeItemPlan mergeItemPlan = emc.find(id, MergeItemPlan.class);
			if (null == mergeItemPlan) {
				throw new ExceptionEntityNotExist(id);
			}
			emc.beginTransaction(MergeItemPlan.class);
			emc.remove(mergeItemPlan, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(mergeItemPlan.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 8432749524458213866L;

	}
}
