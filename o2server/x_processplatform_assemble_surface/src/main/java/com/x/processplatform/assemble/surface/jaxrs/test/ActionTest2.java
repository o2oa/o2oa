package com.x.processplatform.assemble.surface.jaxrs.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;

class ActionTest2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest2.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Work> cq = cb.createQuery(Work.class);
			Root<Work> root = cq.from(Work.class);
			List<Work> list = em.createQuery(cq.select(root)).setMaxResults(1).getResultList();
			Work newWork = new Work();
			emc.persist(newWork);
			return result;
		}
	}
}
