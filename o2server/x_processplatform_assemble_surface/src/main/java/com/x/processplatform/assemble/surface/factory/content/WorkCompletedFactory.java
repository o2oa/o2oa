package com.x.processplatform.assemble.surface.factory.content;

import java.util.ArrayList;
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
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;

public class WorkCompletedFactory extends AbstractFactory {

	public WorkCompletedFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	/**
	 * 根据job获取Work Id
	 */
	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.job), job);
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<WorkCompleted> listWithJobObject(String job) throws Exception {
		List<String> ids = this.listWithJob(job);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<WorkCompleted>();
		}
		return this.business().entityManagerContainer().list(WorkCompleted.class, ids);
	}

	/**
	 * 统计指定人员创建的WorkCompleted数量
	 */
	public Long countWithCreatorPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), person);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 统计指定人员创建的指定应用WorkCompleted数量
	 */
	public Long countWithCreatorPersonApplication(String person, String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), person);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), application));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

//	public Long countWithAttachment(String attachment) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
//		Predicate p = cb.isMember(attachment, root.get(WorkCompleted_.attachmentList));
//		cq.select(cb.count(root)).where(p);
//		return em.createQuery(cq).getSingleResult();
//	}

	public Long countWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.job), job);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listJobWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.process), processId);
		return em.createQuery(cq.select(root.get(WorkCompleted_.job)).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.process), id);
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listJobWithApplication(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.application), applicationId);
		return em.createQuery(cq.select(root.get(WorkCompleted_.job)).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.application), id);
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}