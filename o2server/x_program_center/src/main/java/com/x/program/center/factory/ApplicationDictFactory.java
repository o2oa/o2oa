package com.x.program.center.factory;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDict_;
import com.x.program.center.AbstractFactory;
import com.x.program.center.Business;

public class ApplicationDictFactory extends AbstractFactory {

	public ApplicationDictFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDict> root = cq.from(ApplicationDict.class);
		Predicate p = cb.equal(root.get(ApplicationDict_.application), applicationId);
		cq.select(root.get(ApplicationDict_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<ApplicationDict> listWithApplicationObject(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDict> cq = cb.createQuery(ApplicationDict.class);
		Root<ApplicationDict> root = cq.from(ApplicationDict.class);
		Predicate p = cb.equal(root.get(ApplicationDict_.application), applicationId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public String getWithUniqueName(String uniqueName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDict> root = cq.from(ApplicationDict.class);
		Predicate p = cb.equal(root.get(ApplicationDict_.name), uniqueName);
		p = cb.or(p, cb.equal(root.get(ApplicationDict_.alias), uniqueName));
		p = cb.or(p, cb.equal(root.get(ApplicationDict_.id), uniqueName));
		p = cb.and(p, cb.equal(root.get(ApplicationDict_.application), ApplicationDict.PROJECT_SERVICE));
		cq.select(root.get(ApplicationDict_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public <T extends ApplicationDict> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(ApplicationDict::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}
