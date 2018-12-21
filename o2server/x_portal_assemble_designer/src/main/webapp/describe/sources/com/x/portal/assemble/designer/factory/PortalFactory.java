package com.x.portal.assemble.designer.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

public class PortalFactory extends AbstractFactory {

	public PortalFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.equal(root.get(Portal_.name), name);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAlias(String alias) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.equal(root.get(Portal_.alias), alias);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id))).getResultList();
		return list;
	}

	public <T extends Portal> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(Portal::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(Portal::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}