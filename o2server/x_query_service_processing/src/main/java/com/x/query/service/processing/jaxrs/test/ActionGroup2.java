package com.x.query.service.processing.jaxrs.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.segment.Word;
import com.x.query.core.entity.segment.Word_;

class ActionGroup2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGroup2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			EntityManager em = emc.get(Word.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object> cq = cb.createQuery(Object.class);
			Root<Word> root = cq.from(Word.class);
			Path<String> path = root.get(Word_.value);
			cq.multiselect(cb.count(path), cb.count(path)).groupBy(path);
			List<Object> os = em.createQuery(cq).getResultList();
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}