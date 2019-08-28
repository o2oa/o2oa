package com.x.processplatform.assemble.surface.jaxrs.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

class ActionTest1 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest1.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Work> root = cq.from(Work.class);
			Expression<String> groupByExp = root.get(Work_.application).as(String.class);
			Expression<Double> countExp = cb.avg(root.get(Work_.distributeFactor));
			cq.multiselect(groupByExp, countExp);
			cq.groupBy(groupByExp);
			cq.having(cb.gt(cb.count(root), 5));
			cq.orderBy(cb.desc(countExp));
			logger.debug(effectivePerson, "criteriaQuery:{}", cq);
			List<Tuple> list = em.createQuery(cq).getResultList();
			for (Tuple o : list) {
				System.out.println(o.get(groupByExp) + ":" + o.get(countExp));
			}
			return result;
			// //
			// cq.select(root.get(Work_.process),cb.count(root)).groupBy(root.get(Work_.process));
			// cq.multiselect(root.get(Work_.process),
			// cb.count(root.get(Work_.process))).groupBy(root.get(Work_.process));
			// System.out.println("!!!!!!");
			// System.out.println(cq.toString());
			// System.out.println("!!!!!!");
			// List<Tuple> list = em.createQuery(cq).getResultList();
			// System.out.println(list.size());
			// for (Object o : list) {
			// System.out.println(XGsonBuilder.toJson(o));
			// }
			// return result;
		}
	}
}
