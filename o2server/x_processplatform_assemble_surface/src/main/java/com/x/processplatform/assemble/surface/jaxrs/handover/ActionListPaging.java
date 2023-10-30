package com.x.processplatform.assemble.surface.jaxrs.handover;

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
import com.x.processplatform.core.entity.content.Handover;
import com.x.processplatform.core.entity.content.HandoverSchemeEnum;
import com.x.processplatform.core.entity.content.HandoverStatusEnum;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(Handover.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			List<Wo> wos = emc.fetchDescPaging(Handover.class, Wo.copier, p, page, size, Handover.sequence_FIELDNAME);
			wos.stream().forEach(wo -> {
				wo.setStatus(HandoverStatusEnum.getNameByValue(wo.getStatus()));
				wo.setScheme(HandoverSchemeEnum.getNameByValue(wo.getScheme()));
			});
			result.setData(wos);
			result.setCount(emc.count(Handover.class, p));
			return result;
		}
	}

	public static class Wo extends Handover {

		private static final long serialVersionUID = -4409718421906673933L;

		static WrapCopier<Handover, Wo> copier = WrapCopierFactory.wo(Handover.class, Wo.class,
				JpaObject.singularAttributeField(Handover.class, true,true),
				null);

	}
}
