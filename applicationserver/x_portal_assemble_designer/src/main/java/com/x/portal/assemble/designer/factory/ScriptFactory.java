package com.x.portal.assemble.designer.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Script_;

public class ScriptFactory extends AbstractFactory {

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithIdWithPortal(String id, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.id), id);
		p = cb.and(p, cb.equal(root.get(Script_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithNameWithPortal(String name, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), name);
		p = cb.and(p, cb.equal(root.get(Script_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAliasWithPortal(String alias, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.alias), alias);
		p = cb.and(p, cb.equal(root.get(Script_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).getResultList();
		return list;
	}

	public List<Script> listObjectWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.portal), portalId);
		List<Script> list = em.createQuery(cq.select(root).where(p)).getResultList();
		return list;
	}

}