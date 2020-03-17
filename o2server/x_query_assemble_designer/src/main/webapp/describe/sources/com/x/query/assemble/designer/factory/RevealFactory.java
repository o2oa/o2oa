package com.x.query.assemble.designer.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.AbstractFactory;
import com.x.query.assemble.designer.Business;
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

}