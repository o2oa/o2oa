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
import com.x.query.core.entity.Item;

class ActionTest8 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest8.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Item.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> root = cq.from(Item.class);
			List<Item> os = em.createQuery(cq.select(root)).setFirstResult(2).setMaxResults(3).getResultList();
			result.setData(os);
			return result;
		}
	}
}
