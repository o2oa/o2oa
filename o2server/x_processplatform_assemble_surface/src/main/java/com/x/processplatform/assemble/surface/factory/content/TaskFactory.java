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

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;

public class TaskFactory extends AbstractFactory {

	public TaskFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithWork(Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.work), work.getId());
		cq.select(root.get(Task_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Task> listWithWorkObject(Work work) throws Exception {
		List<String> ids = this.listWithWork(work);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<Task>();
		}
		return this.business().entityManagerContainer().list(Task.class, ids);
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.job), job);
		cq.select(root.get(Task_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Task> listWithJobObject(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.job), job);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countWithPersonWithWork(String person, Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.work), work.getId());
		p = cb.and(p, cb.equal(root.get(Task_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithWork(Work work) throws Exception {
		if (null != work) {
			EntityManager em = this.entityManagerContainer().get(Task.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<Task> root = cq.from(Task.class);
			Predicate p = cb.equal(root.get(Task_.work), work.getId());
			cq.select(cb.count(root)).where(p);
			return em.createQuery(cq).getSingleResult();
		}
		return 0L;
	}

	public String getWithPersonWithWork(String person, String work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.work), work);
		p = cb.and(p, cb.equal(root.get(Task_.person), person));
		cq.select(root.get(Task_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public Long countWithPersonWithJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.job), job);
		p = cb.and(p, cb.equal(root.get(Task_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listPersonWithWork(String work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.work), work);
		cq.select(root.get(Task_.person)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Task> listWithPersonObject(String person, Boolean isExcludeDraft) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), person);
		if (BooleanUtils.isTrue(isExcludeDraft)) {
			p = cb.and(p, cb.or(cb.isFalse(root.get(Task_.first)), cb.isNull(root.get(Task_.first)),
					cb.equal(root.get(Task_.workCreateType), Work.WORKCREATETYPE_ASSIGN)));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get(Task_.createTime)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 统计指定人员在指定应用的待办,数量
	 */
	public Long countWithPersonApplication(String person, String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), person);
		p = cb.and(p, cb.equal(root.get(Task_.application), application));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithActivityToken(String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.activityToken), activityToken);
		cq.select(root.get(Task_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countWithWorkModified(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.modified), true);
		p = cb.and(p, cb.equal(root.get(Task_.work), workId));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/** 原用于前台将task 绑定到workLog 现废弃 */
	// public <W extends WorkLog> List<Task> listTask(W workLog) throws Exception {
	// List<String> ids =
	// this.listWithActivityToken(workLog.getFromActivityToken());
	// List<Task> list = this.entityManagerContainer().list(Task.class, ids);
	// list = list.stream().sorted(Comparator.comparing(Task::getCreateTime,
	// Comparator.nullsLast(Date::compareTo)))
	// .collect(Collectors.toList());
	// return list;
	// }

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.process), id);
		cq.select(root.get(Task_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.application), id);
		cq.select(root.get(Task_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends Task> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}
