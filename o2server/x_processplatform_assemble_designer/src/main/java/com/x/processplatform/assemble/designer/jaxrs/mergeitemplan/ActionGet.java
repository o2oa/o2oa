package com.x.processplatform.assemble.designer.jaxrs.mergeitemplan;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.log.MergeItemPlan;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			MergeItemPlan mergeItemPlan = emc.find(id, MergeItemPlan.class);
			Wo wo = Wo.copier.copy(mergeItemPlan);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends MergeItemPlan {

		private static final long serialVersionUID = 2475165883507548650L;

		static WrapCopier<MergeItemPlan, Wo> copier = WrapCopierFactory.wo(MergeItemPlan.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
