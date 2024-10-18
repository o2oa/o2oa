package com.x.processplatform.assemble.surface.factory.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class ReadCompletedFactory extends AbstractFactory {

	public ReadCompletedFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithWork(Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.work), work.getId());
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<ReadCompleted> listWithWorkObject(Work work) throws Exception {
		List<String> ids = this.listWithWork(work);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<ReadCompleted>();
		}
		return this.business().entityManagerContainer().list(ReadCompleted.class, ids);
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.job), job);
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithWorkCompleted(String workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.workCompleted), workCompleted);
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countWithPersonWithWork(String person, Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.work), work.getId());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithWorkCompleted(String person, WorkCompleted workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.workCompleted), workCompleted.getId());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.job), job);
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/* 统计指定人员的已阅数量 */
	public Long countWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), person);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/* 统计指定人员在指定应用的已阅数量 */
	public Long countWithPersonApplication(String person, String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), person);
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.application), application));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.process), id);
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcessWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.process), id);
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.completed), completed));
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.application), id);
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplicationWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.application), id);
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.completed), completed));
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends ReadCompleted> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(ReadCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}