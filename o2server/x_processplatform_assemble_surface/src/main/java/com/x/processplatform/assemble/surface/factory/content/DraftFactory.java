package com.x.processplatform.assemble.surface.factory.content;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Draft_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class DraftFactory extends AbstractFactory {

	public DraftFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Draft.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Draft> root = cq.from(Draft.class);
		Predicate p = cb.equal(root.get(Draft_.application), id);
		cq.select(root.get(Draft_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Draft.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Draft> root = cq.from(Draft.class);
		Predicate p = cb.equal(root.get(Draft_.process), id);
		cq.select(root.get(Draft_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}