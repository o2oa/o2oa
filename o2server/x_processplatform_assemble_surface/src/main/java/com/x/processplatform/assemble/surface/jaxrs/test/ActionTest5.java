package com.x.processplatform.assemble.surface.jaxrs.test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.Begin;

class ActionTest5 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest5.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Begin.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Begin> cq = cb.createQuery(Begin.class);
			Root<Begin> root = cq.from(Begin.class);
			Predicate p1 = cb.disjunction();
			Predicate p2 = cb.disjunction();
			Predicate p3 = cb.conjunction();
			Predicate p4 = cb.conjunction();
			return result;
		}
	}
}
