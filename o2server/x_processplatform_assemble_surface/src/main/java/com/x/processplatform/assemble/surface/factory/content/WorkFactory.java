package com.x.processplatform.assemble.surface.factory.content;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

public class WorkFactory extends AbstractFactory {

	public WorkFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Long countWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.job), job);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 根据job获取Work Id
	 */
	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.job), job);
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Work> listWithJobObject(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Work> cq = cb.createQuery(Work.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.job), job);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public String getWithActivityToken(String token) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.activityToken), token);
		cq.select(root.get(Work_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * 统计指定人员创建的Work数量
	 */
	public Long countWithCreatorPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), person);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 统计指定人员创建的指定应用Work数量
	 */
	public Long countWithCreatorPersonWithApplication(String person, String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), person);
		p = cb.and(p, cb.equal(root.get(Work_.application), application));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	// public Long countWithAttachment(String attachment) throws Exception {
	// EntityManager em = this.entityManagerContainer().get(Work.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<Long> cq = cb.createQuery(Long.class);
	// Root<Work> root = cq.from(Work.class);
	// Predicate p = cb.isMember(attachment, root.get(Work_.attachmentList));
	// cq.select(cb.count(root)).where(p);
	// return em.createQuery(cq).getSingleResult();
	// }

	public List<String> listWithActivityToken(String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.activityToken), activityToken);
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listJobWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.process), processId);
		return em.createQuery(cq.select(root.get(Work_.job)).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.process), id);
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listJobWithApplication(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), applicationId);
		return em.createQuery(cq.select(root.get(Work_.job)).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), id);
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}