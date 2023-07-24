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
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.assemble.bam.stub.ProcessStubs;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;

public class TimerStartWorkApplicationStubs extends BaseAction {
	public ApplicationStubs execute(EntityManagerContainer emc) throws Exception {
		Business business = new Business(emc);
		DateRange dateRange = this.getDateRange();
		Set<String> ids = new HashSet<>();
		ids.addAll(this.listApplicationFromWork(business, dateRange));
		ids.addAll(this.listApplicationFromWorkCompleted(business, dateRange));
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

	private Collection<String> listApplicationFromWork(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.startTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(Work_.application)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listApplicationFromWorkCompleted(Business business, DateRange dateRange)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(WorkCompleted_.application)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private String getApplicationName(Business business, DateRange dateRange, String applicationId) throws Exception {
		String value = this.getApplicationNameFromWork(business, dateRange, applicationId);
		if (null == value) {
			value = this.getApplicationNameFromWorkCompleted(business, dateRange, applicationId);
		}
		return StringUtils.trimToEmpty(value);
	}

	private String getApplicationNameFromWork(Business business, DateRange dateRange, String applicationId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Work_.application), applicationId));
		cq.select(root.get(Work_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private String getApplicationNameFromWorkCompleted(Business business, DateRange dateRange, String applicationId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private ProcessStubs concreteProcessStubs(Business business, DateRange dateRange, ApplicationStub applicationStub)
			throws Exception {
		/* 所有属于applicationId的processId总和 */
		Set<String> ids = new HashSet<>();
		ids.addAll(this.listProcessFromWork(business, dateRange, applicationStub));
		ids.addAll(this.listProcessFromWorkCompleted(business, dateRange, applicationStub));
		List<ProcessStub> list = new ArrayList<>();
		for (String str : ids) {
			String name = this.getProcessName(business, dateRange, str);
			ProcessStub stub = new ProcessStub();
			stub.setName(name);
			stub.setValue(str);
			stub.setApplicationCategory(applicationStub.getCategory());
			stub.setApplicationName(applicationStub.getName());
			stub.setApplicationValue(applicationStub.getValue());
			list.add(stub);
		}
		SortTools.asc(list, "name");
		ProcessStubs stubs = new ProcessStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listProcessFromWork(Business business, DateRange dateRange,
			ApplicationStub applicationStub) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Work_.application), applicationStub.getValue()));
		cq.select(root.get(Work_.process)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listProcessFromWorkCompleted(Business business, DateRange dateRange,
			ApplicationStub applicationStub) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(root.get(WorkCompleted_.process)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private String getProcessName(Business business, DateRange dateRange, String processId) throws Exception {
		String value = this.getProcessNameFromWork(business, dateRange, processId);
		if (null == value) {
			value = this.getProcessNameFromWorkCompleted(business, dateRange, processId);
		}
		return StringUtils.trimToEmpty(value);
	}

	private String getProcessNameFromWork(Business business, DateRange dateRange, String processId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Work_.process), processId));
		cq.select(root.get(Work_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private String getProcessNameFromWorkCompleted(Business business, DateRange dateRange, String processId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processId));
		cq.select(root.get(WorkCompleted_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

}