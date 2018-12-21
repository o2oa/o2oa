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
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.element.QueryView_;

public class QueryViewFactory extends AbstractFactory {

	public QueryViewFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.equal(root.get(QueryView_.application), application);
		cq.select(root.get(QueryView_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
	
	public List<QueryView> listWithApplicationObject(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueryView> cq = cb.createQuery(QueryView.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.equal(root.get(QueryView_.application), application);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends QueryView> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(QueryView::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}