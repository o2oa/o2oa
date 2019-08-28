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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

class ActionTest7 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest7.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			List<String> os = emc.idsLessThan(Work.class, Work.id_FIELDNAME, "9");

			List<String> ids = new ArrayList<>();
			ids.add("03a4e4e9-23af-44ac-8915-2486f1623d49");
			ids.add("0bf3f186-d115-4cba-8e24-7448aa49fa7b");
			ids.add("6a57cdb0-c58f-4ba2-8f5b-176c61d049bd");
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.isMember(root.get(Work_.id), cb.literal(ids));
			cq.select(root.get(Work_.id)).where(p);
			System.out.println("!!!!!!!TT");
			System.out.println(gson.toJson(em.createQuery(cq).getResultList()));
			System.out.println("!!!!!!!TT");

			// "03a4e4e9-23af-44ac-8915-2486f1623d49",
			// "0bf3f186-d115-4cba-8e24-7448aa49fa7b",
			// "6a57cdb0-c58f-4ba2-8f5b-176c61d049bd"
			return result;
		}
	}
}
