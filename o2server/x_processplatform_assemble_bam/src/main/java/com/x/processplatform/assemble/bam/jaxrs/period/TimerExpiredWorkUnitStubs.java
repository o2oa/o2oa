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

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.DateRange;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.UnitStub;
import com.x.processplatform.assemble.bam.stub.UnitStubs;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;

public class TimerExpiredWorkUnitStubs extends BaseAction {

	public UnitStubs execute(EntityManagerContainer emc) throws Exception {
		DateRange dateRange = this.getDateRange();
		Business business = new Business(emc);
		Set<String> units = new HashSet<>();
		units.addAll(this.listUnitFromWork(business, dateRange));
		units.addAll(this.listUnitFromWorkCompleted(business, dateRange));
		List<UnitStub> list = new ArrayList<>();
		for (String str : units) {
			UnitStub stub = new UnitStub();
			stub.setName(str);
			stub.setValue(str);
			list.add(stub);
		}
		list = list.stream()
				.sorted(Comparator.comparing(UnitStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		UnitStubs stubs = new UnitStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listUnitFromWork(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.between(root.get(Work_.expireTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(Work_.creatorUnit)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listUnitFromWorkCompleted(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.between(root.get(WorkCompleted_.expireTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
		cq.select(root.get(WorkCompleted_.creatorUnit)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	// private DepartmentStubs concreteDepartmentStubs(Business business,
	// DateRange dateRange, CompanyStub companyStub)
	// throws Exception {
	// Set<String> departments = new HashSet<>();
	// departments.addAll(this.listDepartmentFromWork(business, dateRange,
	// companyStub));
	// departments.addAll(this.listDepartmentFromWorkCompleted(business,
	// dateRange, companyStub));
	// List<DepartmentStub> list = new ArrayList<>();
	// for (String str : departments) {
	// DepartmentStub stub = new DepartmentStub();
	// stub.setName(str);
	// stub.setValue(str);
	// stub.setCompanyName(companyStub.getName());
	// stub.setCompanyValue(companyStub.getValue());
	// list.add(stub);
	// }
	// SortTools.asc(list, "name");
	// DepartmentStubs stubs = new DepartmentStubs();
	// stubs.addAll(list);
	// return stubs;
	// }
	//
	// private Collection<String> listDepartmentFromWork(Business business,
	// DateRange dateRange, CompanyStub companyStub)
	// throws Exception {
	// EntityManagerContainer emc = business.entityManagerContainer();
	// EntityManager em = emc.get(Work.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<String> cq = cb.createQuery(String.class);
	// Root<Work> root = cq.from(Work.class);
	// Predicate p = cb.between(root.get(Work_.expireTime),
	// dateRange.getStart(), dateRange.getEnd());
	// p = cb.and(p, cb.equal(root.get(Work_.creatorCompany),
	// companyStub.getValue()));
	// cq.select(root.get(Work_.creatorDepartment)).distinct(true).where(p);
	// List<String> list = em.createQuery(cq).getResultList();
	// return list;
	// }
	//
	// private Collection<String> listDepartmentFromWorkCompleted(Business
	// business, DateRange dateRange,
	// CompanyStub companyStub) throws Exception {
	// EntityManagerContainer emc = business.entityManagerContainer();
	// EntityManager em = emc.get(WorkCompleted.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<String> cq = cb.createQuery(String.class);
	// Root<WorkCompleted> root = cq.from(WorkCompleted.class);
	// Predicate p = cb.between(root.get(WorkCompleted_.expireTime),
	// dateRange.getStart(), dateRange.getEnd());
	// p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
	// p = cb.and(p, cb.equal(root.get(WorkCompleted_.creatorCompany),
	// companyStub.getValue()));
	// cq.select(root.get(WorkCompleted_.creatorDepartment)).distinct(true).where(p);
	// List<String> list = em.createQuery(cq).getResultList();
	// return list;
	// }
}