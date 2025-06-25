package com.x.processplatform.assemble.designer.jaxrs.mergeitemplan;

import java.util.Arrays;
import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.log.MergeItemPlan;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			emc.beginTransaction(MergeItemPlan.class);
			MergeItemPlan mergeItemPlan = new MergeItemPlan();
			Wi.copier.copy(wi, mergeItemPlan);
			mergeItemPlan.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			mergeItemPlan.setLastUpdateTime(new Date());
			emc.persist(mergeItemPlan, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(mergeItemPlan.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 3031463437408581702L;

	}

	public static class Wi extends MergeItemPlan {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, MergeItemPlan> copier = WrapCopierFactory.wi(Wi.class, MergeItemPlan.class, null,
				Arrays.asList(JpaObject.id_FIELDNAME, JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME,
						MergeItemPlan.lastUpdateTime_FIELDNAME, MergeItemPlan.lastUpdatePerson_FIELDNAME,
						MergeItemPlan.estimatedCount_FIELDNAME));

	}
}
