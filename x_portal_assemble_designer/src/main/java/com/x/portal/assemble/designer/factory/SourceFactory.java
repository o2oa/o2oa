package com.x.portal.assemble.designer.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Source;
import com.x.portal.core.entity.Source_;

public class SourceFactory extends AbstractFactory {

	public SourceFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithNameWithPortal(String name, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Source.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Source> root = cq.from(Source.class);
		Predicate p = cb.equal(root.get(Source_.name), name);
		p = cb.and(p, cb.equal(root.get(Source_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Source_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAliasWithPortal(String alias, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Source.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Source> root = cq.from(Source.class);
		Predicate p = cb.equal(root.get(Source_.alias), alias);
		p = cb.and(p, cb.equal(root.get(Source_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Source_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Source.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Source> root = cq.from(Source.class);
		Predicate p = cb.equal(root.get(Source_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Source_.id)).where(p)).getResultList();
		return list;
	}

}