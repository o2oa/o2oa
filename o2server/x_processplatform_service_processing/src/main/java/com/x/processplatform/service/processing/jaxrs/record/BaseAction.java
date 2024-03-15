package com.x.processplatform.service.processing.jaxrs.record;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;

abstract class BaseAction extends StandardJaxrsAction {

	protected static void elapsed(Record rec) throws Exception {
		rec.getProperties()
				.setElapsed(Config.workTime().betweenMinutes(rec.getProperties().getStartTime(), rec.getRecordTime()));
	}

	protected static boolean checkIfWorkAlreadyCompleted(Business business, Record rec, String job) throws Exception {
		WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
				WorkCompleted.job_FIELDNAME, job);
		if (null != workCompleted) {
			rec.setCompleted(true);
			rec.setWorkCompleted(workCompleted.getId());
			return true;
		} else {
			return false;
		}
	}
	
	protected static List<String> listJoinInquireTaskCompletedIdentityWithActivityToken(Business business, String job,
			String activityToken) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
		p = cb.and(p, cb.or(cb.equal(root.get(TaskCompleted_.activityToken), activityToken),
				cb.equal(root.get(TaskCompleted_.joinInquire), Boolean.TRUE)));
		return em.createQuery(cq.select(root.get(TaskCompleted_.identity)).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	/**
	 * 获取所有待办
	 * 
	 * @param business
	 * @param job
	 * @param activityToken
	 * @param series
	 * @return
	 * @throws Exception
	 */
	protected static List<Task> listTaskWithSeriesOrActivityToken(Business business, String job, String activityToken,
			String series) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.job), job);
		p = cb.and(p, cb.or(cb.equal(root.get(Task_.activityToken), activityToken),
				cb.equal(root.get(Task_.series), series)));
		return em.createQuery(cq.where(p)).getResultList();
	}

}
