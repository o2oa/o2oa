package com.x.processplatform.assemble.designer.jaxrs.mergeitemplan;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.log.MergeItemPlan;

class ActionListPaging extends BaseAction {

	private static Logger LOGGER = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson.getDistinguishedName(), page, size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(MergeItemPlan.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			List<Wo> wos = emc.fetchDescPaging(MergeItemPlan.class, Wo.copier, p, page, size,
					JpaObject.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(MergeItemPlan.class, p));
			return result;
		}
	}

	public static class Wo extends MergeItemPlan {

		private static final long serialVersionUID = -4409718421906673933L;

		static WrapCopier<MergeItemPlan, Wo> copier = WrapCopierFactory.wo(MergeItemPlan.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}
}
