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
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class TaskCompletedFactory extends AbstractFactory {

	public TaskCompletedFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Long countWithPersonWithActivityToken(String person, String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.activityToken), activityToken);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithWork(String person, Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.work), work.getId());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithWorkCompleted(String person, WorkCompleted workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.workCompleted), workCompleted.getId());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithWork(Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.work), work.getId());
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<TaskCompleted> listWithWorkObject(Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.work), work.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<TaskCompleted> listWithJobObject(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithWorkCompleted(String workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.workCompleted), workCompleted);
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countWithPersonWithJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<TaskCompleted> listWithPersonObject(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), person);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 统计指定人员已办数量
	 */
	public Long countWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), person);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 统计指定人员指定应用的已办数量
	 */
	public Long countWithPersonWithApplication(String person, String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), person);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), application));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/* 根据ActivityToken获取TaskCompleted */
	public List<String> listWithActivityToken(String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.activityToken), activityToken);
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<TaskCompleted> listWithActivityTokenObject(String activityToken) throws Exception {
		List<String> ids = this.listWithActivityToken(activityToken);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<TaskCompleted>();
		}
		return this.business().entityManagerContainer().list(TaskCompleted.class, ids);
	}

	/* 根据Person和Job值获取TaskCompleted */
	public List<String> listWithPersonWithJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPeresonWithActivityToken(String person, String activityToken) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.activityToken), activityToken);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.process), id);
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcessWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.process), id);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.completed), completed));
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.application), id);
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplicationWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.application), id);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.completed), completed));
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
//	/* 获取可以召回的已办点 */
//	public String getAllowRetract(String person, Work work) throws Exception {
//		if (null != work) {
//			Business business = this.business();
//			List<String> ids = business.workLog().listWithArrivedActivityTokenBackward(work.getActivityToken());
//			List<TaskCompleted> taskCompleteds = new ArrayList<>();
//			for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
//				if (o.getFromActivityType().equals(ActivityType.manual)) {
//					List<String> workCompletedIds = business.taskCompleted().listWithPeresonWithActivityToken(person,
//							o.getFromActivityToken());
//					if (!workCompletedIds.isEmpty()) {
//						taskCompleteds
//								.addAll(business.entityManagerContainer().list(TaskCompleted.class, workCompletedIds));
//					}
//				}
//			}
//			for (TaskCompleted o : taskCompleteds) {
//				if (o.getProcessingType() != ProcessingType.reset) {
//					if (forwardAllowRetract(o)) {
//						return o.getId();
//					}
//				}
//			}
//		}
//		return null;
//	}

//	private boolean forwardAllowRetract(TaskCompleted taskCompleted) throws Exception {
//		Business business = this.business();
//		List<String> ids = business.workLog().listWithFromActivityTokenForward(taskCompleted.getActivityToken());
//		List<ActivityType> deniedActivityTypes = new ArrayList<>();
//		deniedActivityTypes.add(ActivityType.agent);
//		deniedActivityTypes.add(ActivityType.begin);
//		deniedActivityTypes.add(ActivityType.invoke);
//		deniedActivityTypes.add(ActivityType.service);
//		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
//			if (deniedActivityTypes.contains(o.getFromActivityType())) {
//				return false;
//			} else if ((o.getFromActivityType().equals(ActivityType.manual))
//					&& (!StringUtils.equals(o.getFromActivityToken(), taskCompleted.getActivityToken()))
//					&& o.getConnected() == true) {
//				return false;
//			}
//		}
//		return true;
//	}

	/** 原用于前台将taskCompleted 绑定到workLog 现废弃 */
	// public <W extends WorkLog> List<TaskCompleted> listTaskCompleted(W workLog)
	// throws Exception {
	// List<String> ids =
	// this.listWithActivityToken(workLog.getFromActivityToken());
	// List<TaskCompleted> os =
	// this.entityManagerContainer().list(TaskCompleted.class, ids);
	// os = os.stream()
	// .sorted(Comparator.comparing(TaskCompleted::getCompletedTime,
	// Comparator.nullsLast(Date::compareTo)))
	// .collect(Collectors.toList());
	// /** 补充召回 */
	// List<TaskCompleted> list = new ArrayList<>();
	// for (TaskCompleted o : os) {
	// list.add(o);
	// if (o.getProcessingType().equals(ProcessingType.retract)) {
	// TaskCompleted retract = new TaskCompleted();
	// o.copyTo(retract);
	// retract.setRouteName("撤回");
	// retract.setOpinion("撤回");
	// retract.setStartTime(retract.getRetractTime());
	// retract.setCompletedTime(retract.getRetractTime());
	// list.add(retract);
	// }
	// }
	// return list;
	// }

	public <T extends TaskCompleted> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(TaskCompleted::getCompletedTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}