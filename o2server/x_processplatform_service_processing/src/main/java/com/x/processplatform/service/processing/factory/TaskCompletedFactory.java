package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class TaskCompletedFactory extends AbstractFactory {

	public TaskCompletedFactory(Business business) throws Exception {
		super(business);
	}

	/*
	 * 取得最近的TaskCompleted
	 */
	public String getLastWithWork(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.work), id);
		cq.select(root).where(p).orderBy(cb.desc(root.get(TaskCompleted_.createTime)));
		List<TaskCompleted> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0).getId();
		} else {
			return null;
		}
	}

	public List<String> listWithWork(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.work), id);
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

//	public List<String> listWithJob(String job) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
//		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
//		cq.select(root.get(TaskCompleted_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithActivityTokenInIdentityList(String activityToken, List<String> identites)
//			throws Exception {
//		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
//		Predicate p = cb.equal(root.get(TaskCompleted_.activityToken), activityToken);
//		p = cb.and(p, root.get(TaskCompleted_.identity).in(identites));
//		cq.select(root.get(TaskCompleted_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithActivityToken(String activityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
//		Predicate p = cb.equal(root.get(TaskCompleted_.activityToken), activityToken);
//		cq.select(root.get(TaskCompleted_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

}