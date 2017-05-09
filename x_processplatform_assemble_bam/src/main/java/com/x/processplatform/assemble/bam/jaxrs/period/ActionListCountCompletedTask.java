package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

class ActionListCountCompletedTask extends ActionBase {

	ActionResult<List<WrapOutMap>> execute(String applicationId, String processId, String activityId,
			String companyName, String departmentName, String personName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutMap>> result = new ActionResult<>();
			List<WrapOutMap> list = new ArrayList<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			for (DateRange o : os) {
				WrapOutMap wrap = new WrapOutMap();
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				wrap.put("name", str);
				wrap.put("value", str);
				Long count = this.count(business, o, applicationId, processId, activityId, companyName, departmentName,
						personName);
				wrap.put("count", count);
				Long duration = this.duration(business, o, applicationId, processId, activityId, companyName,
						departmentName, personName);
				wrap.put("duration", duration);
				list.add(wrap);
			}
			result.setData(list);
			return result;
		}
	}

	protected Long count(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		}
		if (!StringUtils.equals(activityId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityId));
		}
		if (!StringUtils.equals(companyName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyName));
		}
		if (!StringUtils.equals(departmentName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.department), departmentName));
		}
		if (!StringUtils.equals(personName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long duration(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		}
		if (!StringUtils.equals(activityId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityId));
		}
		if (!StringUtils.equals(companyName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyName));
		}
		if (!StringUtils.equals(departmentName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.department), departmentName));
		}
		if (!StringUtils.equals(personName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), personName));
		}
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}