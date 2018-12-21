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
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

public class TimerCompletedTaskUnitStubs extends BaseAction {

	public UnitStubs execute(EntityManagerContainer emc) throws Exception {
		Business business = new Business(emc);
		DateRange dateRange = this.getDateRange();
		Set<String> units = new HashSet<>();
		units.addAll(this.listUnitFromTaskCompleted(business, dateRange));
		List<UnitStub> list = new ArrayList<>();
		for (String str : units) {
			UnitStub stub = new UnitStub();
			stub.setName(str);
			stub.setValue(str);
			list.add(stub);
		}
		list = list.stream().sorted(Comparator.comparing(UnitStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		UnitStubs stubs = new UnitStubs();
		stubs.addAll(list);
		return stubs;
	}

	private Collection<String> listUnitFromTaskCompleted(Business business, DateRange dateRange) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.between(root.get(TaskCompleted_.completedTime), dateRange.getStart(), dateRange.getEnd());
		cq.select(root.get(TaskCompleted_.unit)).distinct(true).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

}