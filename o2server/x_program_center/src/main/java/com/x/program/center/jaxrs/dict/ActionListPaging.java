package com.x.program.center.jaxrs.dict;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDict_;
import com.x.program.center.Business;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(ApplicationDict.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ApplicationDict> cq = cb.createQuery(ApplicationDict.class);
			Root<ApplicationDict> root = cq.from(ApplicationDict.class);
			Predicate p = cb.equal(root.get(ApplicationDict_.project), ApplicationDict.PROJECT_SERVICE);
			List<Wo> wos = emc.fetchDescPaging(ApplicationDict.class, Wo.copier, p, page, size,
					ApplicationDict.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(ApplicationDict.class, p));
			return result;
		}
	}

	public static class Wo extends ApplicationDict {

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
