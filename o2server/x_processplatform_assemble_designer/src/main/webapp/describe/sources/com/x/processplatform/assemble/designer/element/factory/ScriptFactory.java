package com.x.processplatform.assemble.designer.element.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Script_;

public class ScriptFactory extends AbstractFactory {

	public ScriptFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.application), applicationId);
		cq.select(root.get(Script_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Script> listWithApplicationObject(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.application), applicationId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public String getWithApplicationWithName(String application, String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.alias), name);
		p = cb.or(p, cb.equal(root.get(Script_.name), name));
		p = cb.and(p, cb.equal(root.get(Script_.application), application));
		cq.select(root.get(Script_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public <T extends Script> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Script::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}