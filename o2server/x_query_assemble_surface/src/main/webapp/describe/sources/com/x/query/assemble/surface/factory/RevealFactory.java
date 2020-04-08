package com.x.query.assemble.surface.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.surface.AbstractFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Reveal_;

public class RevealFactory extends AbstractFactory {

	public RevealFactory(Business business) throws Exception {
		super(business);
	}

	public <T extends Reveal> List<T> sort(List<T> list) {
		if (null == list) {
			return null;
		}
		list = list.stream()
				.sorted(Comparator.comparing(Reveal::getAlias, StringTools.emptyLastComparator())
						.thenComparing(Comparator.comparing(Reveal::getName, StringTools.emptyLastComparator())))
				.collect(Collectors.toList());
		return list;
	}

	public List<String> listWithQuery(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.equal(root.get(Reveal_.query), queryId);
		cq.select(root.get(Reveal_.id)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	public List<Reveal> listWithQueryObject(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reveal> cq = cb.createQuery(Reveal.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.equal(root.get(Reveal_.query), queryId);
		cq.select(root).where(p);
		List<Reveal> os = em.createQuery(cq).getResultList();
		return os;
	}
	
	public String getWithQuery(String flag, Query query) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.equal(root.get(Reveal_.query), query.getId());
		p = cb.and(p, cb.or(cb.equal(root.get(Reveal_.name), flag), cb.equal(root.get(Reveal_.id), flag),
				cb.equal(root.get(Reveal_.alias), flag)));
		List<String> os = em.createQuery(cq.select(root.get(Reveal_.id)).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	// public Reveal getWithQueryObject(String flag, Query query) throws Exception {
	// if (StringUtils.isEmpty(flag)) {
	// return null;
	// }
	// EntityManager em = this.entityManagerContainer().get(Reveal.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<Reveal> cq = cb.createQuery(Reveal.class);
	// Root<Reveal> root = cq.from(Reveal.class);
	// Predicate p = cb.equal(root.get(Reveal_.query), query.getId());
	// p = cb.and(p, cb.or(cb.equal(root.get(Reveal_.name), flag),
	// cb.equal(root.get(Reveal_.id), flag),
	// cb.equal(root.get(Reveal_.alias), flag)));
	// List<Reveal> os =
	// em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
	// if (os.isEmpty()) {
	// return null;
	// } else {
	// return os.get(0);
	// }
	// }

}