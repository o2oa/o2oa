package com.x.query.assemble.designer.jaxrs.view;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.entity.View_;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean idleName(Business business, View view) throws Exception {
		EntityManager em = business.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), view.getQuery());
		p = cb.and(p, cb.equal(root.get(View_.name), view.getName()));
		p = cb.and(p, cb.notEqual(root.get(View_.id), view.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

	protected boolean idleAlias(Business business, View view) throws Exception {
		EntityManager em = business.entityManagerContainer().get(View.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<View> root = cq.from(View.class);
		Predicate p = cb.equal(root.get(View_.query), view.getQuery());
		p = cb.and(p, cb.equal(root.get(View_.alias), view.getAlias()));
		p = cb.and(p, cb.notEqual(root.get(View_.id), view.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

	protected Integer getCount(View view, Integer count) {
		Integer viewCount = view.getCount();
		Integer wiCount = ((count == null) || (count < 1) || (count > View.MAX_COUNT)) ? View.MAX_COUNT : count;
		return NumberUtils.min(viewCount, wiCount);
	}

	protected void setProcessEdition(Business business, ProcessPlatformPlan processPlatformPlan) throws Exception {
		if(!processPlatformPlan.where.processList.isEmpty()){
			List<String> _process_ids = ListTools.extractField(processPlatformPlan.where.processList, Process.id_FIELDNAME, String.class,
					true, true);
			List<Process> processList = business.process().listObjectWithProcess(_process_ids, true);
			List<ProcessPlatformPlan.WhereEntry.ProcessEntry> listProcessEntry = gson.fromJson(gson.toJson(processList),
					new TypeToken<List<ProcessPlatformPlan.WhereEntry.ProcessEntry>>(){}.getType());
			if(!listProcessEntry.isEmpty()) {
				processPlatformPlan.where.processList = listProcessEntry;
			}
		}
	}

}
