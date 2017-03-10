package com.x.processplatform.assemble.surface.jaxrs.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

class ActionTest2 extends ActionBase {
	ActionResult<Object> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Work> root = cq.from(Work.class);
			// cq.select(root.get(Work_.process),cb.count(root)).groupBy(root.get(Work_.process));
			cq.multiselect(root.get(Work_.process), cb.count(root.get(Work_.process))).groupBy(root.get(Work_.process));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(cq.toString());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
			List<Tuple> list = em.createQuery(cq).getResultList();
			System.out.println(list.size());
			for (Object o : list) {
				System.out.println(XGsonBuilder.toJson(o));
			}
			return result;
		}
	}
}
