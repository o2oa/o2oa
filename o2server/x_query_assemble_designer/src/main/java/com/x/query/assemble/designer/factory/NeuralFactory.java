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
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.Stat_;

public class NeuralFactory extends AbstractFactory {

	public NeuralFactory(Business business) throws Exception {
		super(business);
	}

	public <T extends Stat> List<T> sort(List<T> list) {
		if (null == list) {
			return null;
		}
		list = list.stream()
				.sorted(Comparator.comparing(Stat::getAlias, StringTools.emptyLastComparator())
						.thenComparing(Comparator.comparing(Stat::getName, StringTools.emptyLastComparator())))
				.collect(Collectors.toList());
		return list;
	}

	public List<String> listWithQuery(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Stat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Stat> root = cq.from(Stat.class);
		Predicate p = cb.equal(root.get(Stat_.query), queryId);
		cq.select(root.get(Stat_.id)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	public List<Stat> listWithQueryObject(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Stat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Stat> cq = cb.createQuery(Stat.class);
		Root<Stat> root = cq.from(Stat.class);
		Predicate p = cb.equal(root.get(Stat_.query), queryId);
		cq.select(root).where(p);
		List<Stat> os = em.createQuery(cq).getResultList();
		return os;
	}

}