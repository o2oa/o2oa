package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.DateRange;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ActivityStubs;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.assemble.bam.stub.ProcessStubs;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;

public class TimerExpiredTaskApplicationStubs extends BaseAction {
	public ApplicationStubs execute(EntityManagerContainer emc) throws Exception {
		Business business = new Business(emc);
		DateRange dateRange = this.getDateRange();
		Set<String> ids = new HashSet<>();
		ids.addAll(this.listApplicationFromTask(business, dateRange));
		ids.addAll(this.listApplicationFromTaskCompleted(business, dateRange));
		List<ApplicationStub> list = new ArrayList<>();
		for (String str : ids) {
			String name = this.getApplicationName(business, dateRange, str);
			ApplicationStub stub = new ApplicationStub();
			stub.setName(name);
			stub.setValue(str);
			stub.setProcessStubs(this.concreteProcessStubs(business, dateRange, stub));
			list.add(stub);
		}
		list = list.stream()
				.sorted(Comparator.comparing(ApplicationStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		ApplicationStubs stubs = new ApplicationStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listApplicationFromTask(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(Task_.application)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listApplicationFromTaskCompleted(Business business, DateRange dateRange)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(root.get(TaskCompleted_.application)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private String getApplicationName(Business business, DateRange dateRange, String applicationId) throws Exception {
		String value = this.getApplicationNameFromTask(business, dateRange, applicationId);
		if (null == value) {
			value = this.getApplicationNameFromTaskCompleted(business, dateRange, applicationId);
		}
		return StringUtils.trimToEmpty(value);
	}

	private String getApplicationNameFromTask(Business business, DateRange dateRange, String applicationId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Task_.application), applicationId));
		cq.select(root.get(Task_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private String getApplicationNameFromTaskCompleted(Business business, DateRange dateRange, String applicationId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationId));
		cq.select(root.get(TaskCompleted_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private ProcessStubs concreteProcessStubs(Business business, DateRange dateRange, ApplicationStub applicationStub)
			throws Exception {
		Set<String> ids = new HashSet<>();
		ids.addAll(this.listProcessFromTask(business, dateRange, applicationStub));
		ids.addAll(this.listProcessFromTaskCompleted(business, dateRange, applicationStub));
		List<ProcessStub> list = new ArrayList<>();
		for (String str : ids) {
			String name = this.getProcessName(business, dateRange, str);
			ProcessStub stub = new ProcessStub();
			stub.setName(name);
			stub.setValue(str);
			stub.setApplicationCategory(applicationStub.getCategory());
			stub.setApplicationName(applicationStub.getName());
			stub.setApplicationValue(applicationStub.getValue());
			stub.setActivityStubs(this.concreteActivityStubs(business, dateRange, stub));
			list.add(stub);
		}
		list = list.stream()
				.sorted(Comparator.comparing(ProcessStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		ProcessStubs stubs = new ProcessStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listProcessFromTask(Business business, DateRange dateRange,
			ApplicationStub applicationStub) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Task_.application), applicationStub.getValue()));
		cq.select(root.get(Task_.process)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listProcessFromTaskCompleted(Business business, DateRange dateRange,
			ApplicationStub applicationStub) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(root.get(TaskCompleted_.process)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private String getProcessName(Business business, DateRange dateRange, String processId) throws Exception {
		String value = this.getProcessNameFromTask(business, dateRange, processId);
		if (null == value) {
			value = this.getProcessNameFromTaskCompleted(business, dateRange, processId);
		}
		return StringUtils.trimToEmpty(value);
	}

	private String getProcessNameFromTask(Business business, DateRange dateRange, String processId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Task_.process), processId));
		cq.select(root.get(Task_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private String getProcessNameFromTaskCompleted(Business business, DateRange dateRange, String processId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processId));
		cq.select(root.get(TaskCompleted_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private ActivityStubs concreteActivityStubs(Business business, DateRange dateRange, ProcessStub processStub)
			throws Exception {
		/* 所有属于processId的activityId总和 */
		Set<String> ids = new HashSet<>();
		ids.addAll(this.listActivityFromTask(business, dateRange, processStub));
		ids.addAll(this.listActivityFromTaskCompleted(business, dateRange, processStub));
		List<ActivityStub> list = new ArrayList<>();
		for (String str : ids) {
			String name = this.getActivityName(business, dateRange, str);
			ActivityStub stub = new ActivityStub();
			stub.setName(name);
			stub.setValue(str);
			stub.setApplicationCategory(processStub.getApplicationCategory());
			stub.setApplicationName(processStub.getApplicationName());
			stub.setApplicationValue(processStub.getApplicationValue());
			stub.setProcessName(processStub.getName());
			stub.setProcessValue(processStub.getValue());
			list.add(stub);
		}
		list = list.stream()
				.sorted(Comparator.comparing(ActivityStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		ActivityStubs stubs = new ActivityStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listActivityFromTask(Business business, DateRange dateRange, ProcessStub processStub)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Task_.process), processStub.getValue()));
		cq.select(root.get(Task_.activity)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listActivityFromTaskCompleted(Business business, DateRange dateRange,
			ProcessStub processStub) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(root.get(TaskCompleted_.activity)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private String getActivityName(Business business, DateRange dateRange, String activityId) throws Exception {
		String value = this.getActivityNameFromTask(business, dateRange, activityId);
		if (null == value) {
			value = this.getActivityNameFromTaskCompleted(business, dateRange, activityId);
		}
		return StringUtils.trimToEmpty(value);
	}

	private String getActivityNameFromTask(Business business, DateRange dateRange, String activityId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Task_.activity), activityId));
		cq.select(root.get(Task_.activityName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private String getActivityNameFromTaskCompleted(Business business, DateRange dateRange, String activityId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityId));
		cq.select(root.get(TaskCompleted_.activityName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}
}