package com.x.processplatform.assemble.surface.jaxrs.test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

class ActionTest4 extends ActionBase {
	ActionResult<Object> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Work> cq = cb.createQuery(Work.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.id), "pp");
			Work o = em.createQuery(cq.where(p)).setMaxResults(1).getSingleResult();
			System.out.println("!!!!!!!!!!!!!!!!!#############");
			System.out.println(o);
			System.out.println("!!!!!!!!!!!!!!!!!#############");
			return result;
		}
	}
}
