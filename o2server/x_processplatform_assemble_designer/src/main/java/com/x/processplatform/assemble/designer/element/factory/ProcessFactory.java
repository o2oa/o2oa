package com.x.processplatform.assemble.designer.element.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

public class ProcessFactory extends AbstractFactory {

	public ProcessFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Process> listWithApplicationObject(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends Process> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Process::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}