package com.x.processplatform.assemble.designer.jaxrs.mergeitemplan;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.log.MergeItemPlan;

class ActionListWithApplicationPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithApplicationPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationId, Integer page, Integer size)
			throws Exception {

		LOGGER.debug("execute:{}, applicationId:{}.", effectivePerson::getDistinguishedName, () -> applicationId);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationId);
			}
			List<MergeItemPlan> os = emc.fetchEqualDescPaging(MergeItemPlan.class, MergeItemPlan.application_FIELDNAME,
					application.getId(), page, size, JpaObject.createTime_FIELDNAME);
			result.setData(Wo.copier.copy(os));
			return result;
		}
	}

	public static class Wo extends MergeItemPlan {

		private static final long serialVersionUID = -4634813946957790700L;

		static WrapCopier<MergeItemPlan, Wo> copier = WrapCopierFactory.wo(MergeItemPlan.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

}
