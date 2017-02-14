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
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;

class ActionListCountCompletedWork extends ActionBase {

	ActionResult<List<WrapOutMap>> execute(String applicationId, String processId, String companyName,
			String departmentName, String personName) throws Exception {
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
				Long count = this.count(business, o, applicationId, processId, companyName, departmentName, personName);
				wrap.put("count", count);
				Long duration = this.duration(business, o, applicationId, processId, companyName, departmentName,
						personName);
				wrap.put("duration", duration);
				Long times = this.times(business, o, applicationId, processId, companyName, departmentName, personName);
				wrap.put("times", times);
				list.add(wrap);
			}
			result.setData(list);
			return result;
		}
	}

	protected Long count(Business business, DateRange dateRange, String applicationId, String processId,
			String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		}
		if (!StringUtils.equals(companyName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorCompany), companyName));
		}
		if (!StringUtils.equals(departmentName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorDepartment), departmentName));
		}
		if (!StringUtils.equals(personName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long duration(Business business, DateRange dateRange, String applicationId, String processId,
			String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		}
		if (!StringUtils.equals(companyName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorCompany), companyName));
		}
		if (!StringUtils.equals(departmentName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorDepartment), departmentName));
		}
		if (!StringUtils.equals(personName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), personName));
		}
		cq.select(cb.sum(root.get(WorkCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long times(Business business, DateRange dateRange, String applicationId, String processId,
			String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		}
		if (!StringUtils.equals(companyName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyName));
		}
		if (!StringUtils.equals(departmentName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.department), departmentName));
		}
		if (!StringUtils.equals(personName, HttpAttribute.x_empty_symbol)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}