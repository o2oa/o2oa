package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;

class ActionListCountExpiredTask extends ActionBase {

	ActionResult<List<NameValueCountPair>> execute(String applicationId, String processId, String activityId,
			String companyName, String departmentName, String personName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			List<NameValueCountPair> list = new ArrayList<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			for (DateRange o : os) {
				NameValueCountPair p = new NameValueCountPair();
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				p.setName(str);
				p.setValue(str);
				p.setCount(this.count(business, o, applicationId, processId, activityId, companyName, departmentName,
						personName));
				list.add(p);
			}
			result.setData(list);
			return result;
		}
	}

	protected Long count(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String companyName, String departmentName, String personName) throws Exception {
		Long c = this.countTask(business, dateRange, applicationId, processId, activityId, companyName, departmentName,
				personName);
		c += this.countTaskCompleted(business, dateRange, applicationId, processId, activityId, companyName,
				departmentName, personName);
		return c;
	}

	private Long countTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.application), applicationId));
		}
		if (!StringUtils.equals(processId,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.process), processId));
		}
		if (!StringUtils.equals(activityId,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.activity), activityId));
		}
		if (!StringUtils.equals(companyName,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.company), companyName));
		}
		if (!StringUtils.equals(departmentName,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.department), departmentName));
		}
		if (!StringUtils.equals(personName,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.person), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countTaskCompleted(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		if (!StringUtils.equals(applicationId,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		}
		if (!StringUtils.equals(activityId,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityId));
		}
		if (!StringUtils.equals(companyName,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyName));
		}
		if (!StringUtils.equals(departmentName,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.department), departmentName));
		}
		if (!StringUtils.equals(personName,StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}