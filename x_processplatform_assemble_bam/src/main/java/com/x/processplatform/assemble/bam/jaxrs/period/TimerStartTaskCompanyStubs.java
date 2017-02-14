package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.CompanyStubs;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStubs;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;

public class TimerStartTaskCompanyStubs extends ActionBase {

	public CompanyStubs execute(EntityManagerContainer emc) throws Exception {
		DateRange dateRange = this.getDateRange();
		Business business = new Business(emc);
		Set<String> companies = new HashSet<>();
		companies.addAll(this.listCompanyFromTask(business, dateRange));
		companies.addAll(this.listCompanyFromTaskCompleted(business, dateRange));
		List<CompanyStub> list = new ArrayList<>();
		for (String str : companies) {
			CompanyStub stub = new CompanyStub();
			stub.setName(str);
			stub.setValue(str);
			stub.setDepartmentStubs(this.concreteDepartmentStubs(business, dateRange, stub));
			list.add(stub);
		}
		SortTools.asc(list, "name");
		CompanyStubs stubs = new CompanyStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listCompanyFromTask(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.startTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(Task_.company)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listCompanyFromTaskCompleted(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(TaskCompleted_.company)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private DepartmentStubs concreteDepartmentStubs(Business business, DateRange dateRange, CompanyStub companyStub)
			throws Exception {
		Set<String> departments = new HashSet<>();
		departments.addAll(this.listDepartmentFromTask(business, dateRange, companyStub));
		departments.addAll(this.listDepartmentFromTaskCompleted(business, dateRange, companyStub));
		List<DepartmentStub> list = new ArrayList<>();
		for (String str : departments) {
			DepartmentStub stub = new DepartmentStub();
			stub.setName(str);
			stub.setValue(str);
			stub.setCompanyName(companyStub.getName());
			stub.setCompanyValue(companyStub.getValue());
			list.add(stub);
		}
		SortTools.asc(list, "name");
		DepartmentStubs stubs = new DepartmentStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listDepartmentFromTask(Business business, DateRange dateRange, CompanyStub companyStub)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.between(root.get(Task_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(Task_.company), companyStub.getValue()));
		cq.select(root.get(Task_.department)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	private Collection<String> listDepartmentFromTaskCompleted(Business business, DateRange dateRange,
			CompanyStub companyStub) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.startTime), dateRange.getStart(), dateRange.getEnd());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyStub.getValue()));
		cq.select(root.get(TaskCompleted_.department)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}
}