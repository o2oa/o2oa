package com.x.query.assemble.designer.factory;

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
import com.x.query.assemble.designer.AbstractFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.entity.View_;

public class ViewFactory extends AbstractFactory {

	public ViewFactory(Business business) throws Exception {
		super(business);
	}

	public <T extends View> List<T> sort(List<T> list) {
		if (null == list) {
			return null;
		}
		list = list.stream()
				.sorted(Comparator.comparing(View::getAlias, StringTools.emptyLastComparator())
						.thenComparing(Comparator.comparing(View::getName, StringTools.emptyLastComparator())))
				.collect(Collectors.toList());
		return list;
	}

	public List<String> listWithQuery(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), queryId);
		cq.select(root.get(View_.id)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	public List<View> listWithQueryObject(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<View> cq = cb.createQuery(View.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), queryId);
		cq.select(root).where(p);
		List<View> os = em.createQuery(cq).getResultList();
		return os;
	}

	public View getWithQueryObject(String flag, Query query) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<View> cq = cb.createQuery(View.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), query.getId());
		p = cb.and(p, cb.or(cb.equal(root.get(View_.name), flag), cb.equal(root.get(View_.id), flag),
				cb.equal(root.get(View_.alias), flag)));
		List<View> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}

	}
}