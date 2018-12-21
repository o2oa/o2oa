package com.x.processplatform.assemble.surface.jaxrs.test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

class ActionTest8 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest8.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			EntityManager em = emc.get(Item.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Item> root = cq.from(Item.class);
			List<String> list = new ArrayList<>();
			list.add("875926bc-847f-4a64-929b-410822304bfb");
			list.add("ade2f4b2-4b3c-4662-be3a-61cee55e4c31");
			Predicate p = cb.isMember(root.get(Item_.id), cb.literal(list));
			cq.select(root.get(Item_.path0)).where(p).distinct(true);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(gson.toJson(em.createQuery(cq).getResultList()));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!1111");
			return result;
		}
	}
}
