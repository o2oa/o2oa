package com.x.query.service.processing.jaxrs.word;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
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
import com.x.query.service.processing.Business;

class ActionIdentifier01 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionIdentifier01.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> list = this.list(business);
			logger.debug("total n word:{}.", list.size());
			int identifier = 0;
			for (String str : list) {

			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

	}

	public static class Wo extends WrapBoolean {
	}

	private void update(Business business, String str, Integer identifier) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Word.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Word> cu = cb.createCriteriaUpdate(Word.class);
		Root<Word> root = cu.from(Word.class);
		Predicate p = cb.equal(cb.lower(cb.substring(root.get(Word_.value), 1)), "n");
		p = cb.and(p, cb.equal(root.get(Word_.value), str));
		cu.set(root.get(Word_.identifier01), identifier);
		em.createQuery(cu).executeUpdate();
	}

	private Long count(Business business, String str) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Word.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Word> root = cq.from(Word.class);
		Predicate p = cb.equal(cb.lower(cb.substring(root.get(Word_.label), 1)), "n");
		p = cb.and(p, cb.equal(root.get(Word_.value), str));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	private List<String> list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Word.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Word> root = cq.from(Word.class);
		Predicate p = cb.equal(cb.lower(cb.substring(root.get(Word_.value), 1)), "n");
		List<String> os = em.createQuery(cq.select(root.get(Word_.value)).where(p).distinct(true)).getResultList();
		return os;
	}

}