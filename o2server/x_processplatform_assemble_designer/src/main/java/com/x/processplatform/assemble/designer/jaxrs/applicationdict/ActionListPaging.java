package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.ApplicationDict;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

class ActionListPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson, null)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						"all", "all");
			}
			EntityManager em = emc.get(ApplicationDict.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();;
			List<Wo> wos = emc.fetchDescPaging(ApplicationDict.class, Wo.copier, p, page, size, ApplicationDict.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(ApplicationDict.class, p));
			return result;
		}
	}

	public static class Wo extends ApplicationDict {

		private static final long serialVersionUID = -192812264880120309L;

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
