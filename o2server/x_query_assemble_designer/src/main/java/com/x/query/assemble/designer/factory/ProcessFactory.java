package com.x.query.assemble.designer.factory;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import com.x.query.assemble.designer.AbstractFactory;
import com.x.query.assemble.designer.Business;

public class ProcessFactory extends AbstractFactory {

	public ProcessFactory(Business business) throws Exception {
		super(business);
	}

	public List<Process> listObjectWithProcess(List<String> processList, boolean includeEdition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		p = cb.and(p, root.get(Process_.id).in(processList));
		if(includeEdition){
			p = cb.and(p, cb.isNull(root.get(Process_.editionEnable)));
			Subquery<Process> subquery = cq.subquery(Process.class);
			Root<Process> subRoot = subquery.from(Process.class);
			Predicate subP = cb.conjunction();
			subP = cb.and(subP, cb.equal(root.get(Process_.edition), subRoot.get(Process_.edition)));
			subP = cb.and(subP, subRoot.get(Process_.id).in(processList));
			subP = cb.and(subP, cb.isNotNull(root.get(Process_.edition)));
			subquery.select(subRoot).where(subP);
			p = cb.or(p, cb.exists(subquery));
		}

		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
}