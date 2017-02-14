package com.x.processplatform.service.service.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.service.service.AbstractFactory;
import com.x.processplatform.service.service.Business;

public class WorkFactory extends AbstractFactory {

	public WorkFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithActivity(String activity) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.activity), activity);
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}