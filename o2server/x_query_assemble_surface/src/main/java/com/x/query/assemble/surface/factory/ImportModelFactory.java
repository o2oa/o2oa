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
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.ImportModel_;
import com.x.query.core.entity.Query;

public class ImportModelFactory extends AbstractFactory {

	public ImportModelFactory(Business business) throws Exception {
		super(business);
	}

	public <T extends ImportModel> List<T> sort(List<T> list) {
		if (null == list) {
			return null;
		}
		list = list.stream()
				.sorted(Comparator.comparing(ImportModel::getAlias, StringTools.emptyLastComparator())
						.thenComparing(Comparator.comparing(ImportModel::getName, StringTools.emptyLastComparator())))
				.collect(Collectors.toList());
		return list;
	}

	public List<String> listWithQuery(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ImportModel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ImportModel> root = cq.from(ImportModel.class);
		Predicate p = cb.equal(root.get(ImportModel_.query), queryId);
		cq.select(root.get(ImportModel_.id)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	public List<ImportModel> listWithQueryObject(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ImportModel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ImportModel> cq = cb.createQuery(ImportModel.class);
		Root<ImportModel> root = cq.from(ImportModel.class);
		Predicate p = cb.equal(root.get(ImportModel_.query), queryId);
		cq.select(root).where(p);
		List<ImportModel> os = em.createQuery(cq).getResultList();
		return os;
	}

	public String getWithQuery(String flag, Query query) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(ImportModel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ImportModel> root = cq.from(ImportModel.class);
		Predicate p = cb.equal(root.get(ImportModel_.query), query.getId());
		p = cb.and(p, cb.or(cb.equal(root.get(ImportModel_.name), flag), cb.equal(root.get(ImportModel_.id), flag),
				cb.equal(root.get(ImportModel_.alias), flag)));
		List<String> os = em.createQuery(cq.select(root.get(ImportModel_.id)).where(p)).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

}