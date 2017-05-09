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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;

class ActionListCountExpiredWork extends ActionBase {

	ActionResult<List<NameValueCountPair>> execute(String applicationId, String processId, String companyName,
			String departmentName, String personName) throws Exception {
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
				p.setCount(this.count(business, o, applicationId, processId, companyName, departmentName, personName));
				list.add(p);
			}
			result.setData(list);
			return result;
		}
	}

	protected Long count(Business business, DateRange dateRange, String applicationId, String processId,
			String companyName, String departmentName, String personName) throws Exception {
		Long c = this.countWork(business, dateRange, applicationId, processId, companyName, departmentName, personName);
		c += this.countWorkCompleted(business, dateRange, applicationId, processId, companyName, departmentName,
				personName);
		return c;
	}

	private Long countWork(Business business, DateRange dateRange, String applicationId, String processId,
			String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.expireTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.process), processId));
		}
		if (!StringUtils.equals(companyName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.creatorCompany), companyName));
		}
		if (!StringUtils.equals(departmentName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.creatorDepartment), departmentName));
		}
		if (!StringUtils.equals(personName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countWorkCompleted(Business business, DateRange dateRange, String applicationId, String processId,
			String companyName, String departmentName, String personName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		}
		if (!StringUtils.equals(companyName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorCompany), companyName));
		}
		if (!StringUtils.equals(departmentName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorDepartment), departmentName));
		}
		if (!StringUtils.equals(personName, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), personName));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}