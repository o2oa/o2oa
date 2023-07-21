package com.x.processplatform.assemble.designer.element.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

public class ProcessFactory extends AbstractFactory {

	public ProcessFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String application, boolean justEditionEnable) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		if(justEditionEnable) {
			p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		}
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplications(List<String> applications) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(applications)) {
			p = cb.isMember(root.get(Process_.application), cb.literal(applications));
		}
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Process> listWithApplicationObject(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listProcessEdition(String application, String edition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.equal(root.get(Process_.edition), edition));
		cq.select(root.get(Process_.id)).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		return em.createQuery(cq).getResultList();
	}

	public List<Process> listProcessEditionObject(String application, String edition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.equal(root.get(Process_.edition), edition));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		return em.createQuery(cq).getResultList();
	}

	public List<String> listProcessDisableEdition(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.isNotNull(root.get(Process_.edition)));
		p = cb.and(p, cb.notEqual(root.get(Process_.edition), ""));
		Subquery<Process> subquery = cq.subquery(Process.class);
		Root<Process> subRoot = subquery.from(Process.class);
		Predicate subP = cb.conjunction();
		subP = cb.and(subP, cb.equal(root.get(Process_.edition), subRoot.get(Process_.edition)));
		subP = cb.and(subP, cb.isTrue(subRoot.get(Process_.editionEnable)));
		subquery.select(subRoot).where(subP);
		p = cb.and(p, cb.not(cb.exists(subquery)));
		cq.select(root.get(Process_.edition)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public Process getEnabledProcess(String application, String edition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.equal(root.get(Process_.edition), edition));
		p = cb.and(p, cb.isTrue(root.get(Process_.editionEnable)));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		List<Process> list = em.createQuery(cq).getResultList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public <T extends Process> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Process::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	public Double getMaxEditionNumber(String application, String edition) throws Exception {
		if (StringUtils.isNotEmpty(edition)) {
			EntityManager em = this.entityManagerContainer().get(Process.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Double> cq = cb.createQuery(Double.class);
			Root<Process> root = cq.from(Process.class);
			Predicate p = cb.equal(root.get(Process_.application), application);
			p = cb.and(p, cb.equal(root.get(Process_.edition), edition));
			cq.select(cb.max(root.get(Process_.editionNumber))).where(p);
			Double max = em.createQuery(cq).getSingleResult();
			if (max == null || max < 1.0) {
				max = 1.0;
			}
			return max;
		}
		return 1.0;
	}

}
