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
import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.schema.Table_;

public class TableFactory extends AbstractFactory {

	public TableFactory(Business business) throws Exception {
		super(business);
	}

	public <T extends Table> List<T> sort(List<T> list) {
		if (null == list) {
			return null;
		}
		list = list.stream()
				.sorted(Comparator.comparing(Table::getAlias, StringTools.emptyLastComparator())
						.thenComparing(Comparator.comparing(Table::getName, StringTools.emptyLastComparator())))
				.collect(Collectors.toList());
		return list;
	}

	public List<Table> listWithQueryObject(String queryId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Table.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Table> cq = cb.createQuery(Table.class);
		Root<Table> root = cq.from(Table.class);
		Predicate p = cb.equal(root.get(Table_.query), queryId);
		cq.select(root).where(p);
		List<Table> os = em.createQuery(cq).getResultList();
		return os;
	}

}