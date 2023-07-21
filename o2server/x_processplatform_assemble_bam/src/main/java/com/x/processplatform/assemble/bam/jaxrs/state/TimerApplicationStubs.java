package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ActivityStubs;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.assemble.bam.stub.ProcessStubs;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

public class TimerApplicationStubs {

	public void execute(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root);
		List<Application> os = em.createQuery(cq).getResultList();
		List<ApplicationStub> list = new ArrayList<>();
		for (Application o : os) {
			ApplicationStub stub = new ApplicationStub();
			stub.setName(o.getName());
			stub.setValue(o.getId());
			stub.setProcessStubs(this.concreteProcessStubs(business, o.getId()));
			list.add(stub);
		}
		list = list.stream()
				.sorted(Comparator.comparing(ApplicationStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		ApplicationStubs stubs = new ApplicationStubs();
		stubs.addAll(list);
		ThisApplication.state.setApplicationStubs(stubs);
	}

	private ProcessStubs concreteProcessStubs(Business business, String applicationId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), applicationId);
		cq.select(root).where(p);
		List<Process> os = em.createQuery(cq).getResultList();
		List<ProcessStub> list = new ArrayList<>();
		for (Process o : os) {
			ProcessStub stub = new ProcessStub();
			stub.setName(o.getName());
			stub.setValue(o.getId());
			stub.setActivityStubs(this.concreteActivityStubs(business, o.getId()));
			list.add(stub);
		}
		list = list.stream().sorted(Comparator.comparing(ProcessStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		ProcessStubs stubs = new ProcessStubs();
		stubs.addAll(list);
		return stubs;
	}

	private ActivityStubs concreteActivityStubs(Business business, String processId) throws Exception {
		List<ActivityStub> list = new ArrayList<>();
		list.addAll(this.listActivity(business, Agent.class, processId));
		list.addAll(this.listActivity(business, Begin.class, processId));
		list.addAll(this.listActivity(business, Cancel.class, processId));
		list.addAll(this.listActivity(business, Choice.class, processId));
		list.addAll(this.listActivity(business, Delay.class, processId));
		list.addAll(this.listActivity(business, Embed.class, processId));
		list.addAll(this.listActivity(business, End.class, processId));
		list.addAll(this.listActivity(business, Invoke.class, processId));
		list.addAll(this.listActivity(business, Manual.class, processId));
		list.addAll(this.listActivity(business, Merge.class, processId));
		list.addAll(this.listActivity(business, Parallel.class, processId));
		list.addAll(this.listActivity(business, Service.class, processId));
		list.addAll(this.listActivity(business, Split.class, processId));
		list = list.stream()
				.sorted(Comparator.comparing(ActivityStub::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		ActivityStubs stubs = new ActivityStubs();
		stubs.addAll(list);
		return stubs;
	}

	private <T extends Activity> List<ActivityStub> listActivity(Business business, Class<T> cls, String processId)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get("process"), processId);
		cq.select(root).where(p);
		List<T> os = em.createQuery(cq).getResultList();
		List<ActivityStub> list = new ArrayList<>();
		for (T t : os) {
			ActivityStub stub = new ActivityStub();
			stub.setName(t.getName());
			stub.setValue(t.getId());
			stub.setActivityType(t.getActivityType());
			list.add(stub);
		}
		return list;
	}

}
