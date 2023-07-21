package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DateRange;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;

class BaseAction extends StandardJaxrsAction {

	List<DateRange> listDateRange() throws Exception {
		List<DateRange> list = new ArrayList<>();
		Date now = new Date();
		for (int i = -1; i >= -12; i--) {
			Date start = DateTools.floorMonth(now, i);
			Date end = DateTools.ceilMonth(now, i);
			DateRange range = new DateRange(start, end);
			list.add(range);
		}
		return list;
	}

	DateRange getDateRange() throws Exception {
		Date now = new Date();
		Date start = DateTools.floorMonth(now, -12);
		Date end = DateTools.ceilMonth(now, -1);
		DateRange range = new DateRange(start, end);
		return range;
	}

	/** 统计 */
	protected Long countStartTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String unit, String person) throws Exception {
		List<String> units = new ArrayList<>();
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
		}
		Long c = this.countStartTaskTask(business, dateRange, applicationId, processId, activityId, units, person);
		c += this.countStartTaskTaskCompleted(business, dateRange, applicationId, processId, activityId, units, person);
		return c;
	}

	private Long countStartTaskTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, List<String> units, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.startTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.process), processId));
		}
		if (!StringUtils.equals(activityId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.activity), activityId));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(Task_.unit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.person), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countStartTaskTaskCompleted(Business business, DateRange dateRange, String applicationId,
			String processId, String activityId, List<String> units, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		}
		if (!StringUtils.equals(activityId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityId));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(TaskCompleted_.unit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long countCompletedTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String unit, String person) throws Exception {
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
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			List<String> units = new ArrayList<>();
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
			p = cb.and(p, root.get(TaskCompleted_.unit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long durationCompletedTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String unit, String person) throws Exception {
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
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			List<String> units = new ArrayList<>();
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
			p = cb.and(p, root.get(TaskCompleted_.unit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		}
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long countExpiredTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, String unit, String person) throws Exception {
		List<String> units = new ArrayList<>();
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
		}
		Long c = this.countExpiredTaskTask(business, dateRange, applicationId, processId, activityId, units, person);
		c += this.countExpiredTaskTaskCompleted(business, dateRange, applicationId, processId, activityId, units,
				person);
		return c;
	}

	private Long countExpiredTaskTask(Business business, DateRange dateRange, String applicationId, String processId,
			String activityId, List<String> units, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.process), processId));
		}
		if (!StringUtils.equals(activityId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.activity), activityId));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(Task_.unit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Task_.person), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredTaskTaskCompleted(Business business, DateRange dateRange, String applicationId,
			String processId, String activityId, List<String> units, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		}
		if (!StringUtils.equals(activityId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityId));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(TaskCompleted_.unit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long countStartWork(Business business, DateRange dateRange, String applicationId, String processId,
			String unit, String person) throws Exception {
		List<String> units = new ArrayList<>();
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
		}
		Long c = this.countStartWorkWork(business, dateRange, applicationId, processId, units, person);
		c += this.countStartWorkWorkCompleted(business, dateRange, applicationId, processId, units, person);
		return c;
	}

	private Long countStartWorkWork(Business business, DateRange dateRange, String applicationId, String processId,
			List<String> units, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.startTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.process), processId));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(Work_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countStartWorkWorkCompleted(Business business, DateRange dateRange, String applicationId,
			String processId, List<String> units, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(WorkCompleted_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long countCompletedWork(Business business, DateRange dateRange, String applicationId, String processId,
			String unit, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		}
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			List<String> units = new ArrayList<>();
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
			p = cb.and(p, root.get(WorkCompleted_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long durationCompletedWork(Business business, DateRange dateRange, String applicationId, String processId,
			String unit, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		if (!StringUtils.equals(applicationId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		}
		if (!StringUtils.equals(processId, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		}
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			List<String> units = new ArrayList<>();
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
			p = cb.and(p, root.get(WorkCompleted_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), person));
		}
		cq.select(cb.sum(root.get(WorkCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long timesCompletedWork(Business business, DateRange dateRange, String applicationId, String processId,
			String unit, String person) throws Exception {
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
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			List<String> units = new ArrayList<>();
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
			p = cb.and(p, root.get(TaskCompleted_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	protected Long countExpiredWork(Business business, DateRange dateRange, String applicationId, String processId,
			String unit, String person) throws Exception {
		List<String> units = new ArrayList<>();
		if (!StringUtils.equals(unit, StandardJaxrsAction.EMPTY_SYMBOL)) {
			units.add(unit);
			units.addAll(business.organization().unit().listWithUnitSubNested(unit));
		}
		Long c = this.countExpiredWorkWork(business, dateRange, applicationId, processId, units, person);
		c += this.countExpiredWorkWorkCompleted(business, dateRange, applicationId, processId, units, person);
		return c;
	}

	private Long countExpiredWorkWork(Business business, DateRange dateRange, String applicationId, String processId,
			List<String> units, String person) throws Exception {
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
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(Work_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredWorkWorkCompleted(Business business, DateRange dateRange, String applicationId,
			String processId, List<String> units, String person) throws Exception {
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
		if (ListTools.isNotEmpty(units)) {
			p = cb.and(p, root.get(WorkCompleted_.creatorUnit).in(units));
		}
		if (!StringUtils.equals(person, StandardJaxrsAction.EMPTY_SYMBOL)) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorPerson), person));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}