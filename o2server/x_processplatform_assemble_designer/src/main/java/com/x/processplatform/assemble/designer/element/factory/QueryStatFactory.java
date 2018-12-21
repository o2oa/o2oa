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
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryStat_;

public class QueryStatFactory extends AbstractFactory {

	public QueryStatFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(QueryStat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryStat> root = cq.from(QueryStat.class);
		Predicate p = cb.equal(root.get(QueryStat_.application), application);
		cq.select(root.get(QueryStat_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
	
	public List<QueryStat> listWithApplicationObject(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(QueryStat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueryStat> cq = cb.createQuery(QueryStat.class);
		Root<QueryStat> root = cq.from(QueryStat.class);
		Predicate p = cb.equal(root.get(QueryStat_.application), application);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends QueryStat> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(QueryStat::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}