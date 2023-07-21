package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.ActivityType;

/** 统计按应用,流程,活动节点分类的 */
public class TimerCategory extends ActionBase {

	public void execute(Business business) throws Exception {
		ActionCategory.Wo wo = new ActionCategory.Wo();
		Date start = this.getStart();
		Date current = new Date();
		wo.setApplication(this.application(business, start, current));
		wo.setProcess(this.process(business, start, current));
		wo.setActivity(this.activity(business, start, current));
		ThisApplication.state.setCategory(wo);
	}

	private List<ActionCategory.WoApplication> application(Business business, Date start, Date current)
			throws Exception {
		List<ActionCategory.WoApplication> list = new ArrayList<>();
		for (ApplicationStub stub : ThisApplication.state.getApplicationStubs()) {
			Long taskCount = 0L;
			Long taskExpiredCount = 0L;
			Long taskDuration = 0L;
			Long taskCompletedCount = 0L;
			Long taskCompletedExpiredCount = 0L;
			Long taskCompletedDuration = 0L;
			Long workCount = 0L;
			Long workExpiredCount = 0L;
			Long workDuration = 0L;
			Long workCompletedCount = 0L;
			Long workCompletedExpiredCount = 0L;
			Long workCompletedDuration = 0L;
			taskCount = this.countTask(business, start, stub);
			if (taskCount > 0) {
				taskExpiredCount = this.countTaskExpired(business, start, current, stub);
				taskDuration = this.durationTask(business, start, current, stub);
			}
			taskCompletedCount = this.countTaskCompleted(business, start, stub);
			if (taskCompletedCount > 0) {
				taskCompletedExpiredCount = this.countTaskCompletedExpired(business, start, stub);
				taskCompletedDuration = this.durationTaskCompleted(business, start, stub);
			}
			workCount = this.countWork(business, start, stub);
			if (workCount > 0) {
				workExpiredCount = this.countWorkExpired(business, start, current, stub);
				workDuration = this.durationWork(business, start, current, stub);
			}
			workCompletedCount = this.countWorkCompleted(business, start, stub);
			if (workCompletedCount > 0) {
				workCompletedExpiredCount = this.countWorkCompletedExpired(business, start, stub);
				workCompletedDuration = this.durationWorkCompleted(business, start, stub);
			}
			ActionCategory.WoApplication wo = new ActionCategory.WoApplication();
			wo.setName(stub.getName());
			wo.setValue(stub.getValue());
			wo.setTaskCount(taskCount);
			wo.setTaskExpiredCount(taskExpiredCount);
			wo.setTaskDuration(taskDuration);
			wo.setTaskCompletedCount(taskCompletedCount);
			wo.setTaskCompletedExpiredCount(taskCompletedExpiredCount);
			wo.setTaskCompletedDuration(taskCompletedDuration);
			wo.setWorkCount(workCount);
			wo.setWorkExpiredCount(workExpiredCount);
			wo.setWorkDuration(workDuration);
			wo.setWorkCompletedCount(workCompletedCount);
			wo.setWorkCompletedExpiredCount(workCompletedExpiredCount);
			wo.setWorkCompletedDuration(workCompletedDuration);
			list.add(wo);
		}
		list = list.stream().sorted(
				Comparator.comparing(ActionCategory.WoApplication::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	private Long countTask(Business business, Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countTaskExpired(Business business, Date start, Date current, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Task_.expireTime), current));
		p = cb.and(p, cb.equal(root.get(Task_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationTask(Business business, Date start, Date current, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.application), applicationStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countTaskCompleted(Business business, Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countTaskCompletedExpired(Business business, Date start, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationTaskCompleted(Business business, Date start, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countWork(Business business, Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countWorkExpired(Business business, Date start, Date current, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Work_.expireTime), current));
		p = cb.and(p, cb.equal(root.get(Work_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWork(Business business, Date start, Date current, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.application), applicationStub.getValue()));
		cq.select(root.get(Work_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countWorkCompleted(Business business, Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countWorkCompletedExpired(Business business, Date start, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWorkCompleted(Business business, Date start, ApplicationStub applicationStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(cb.sum(root.get(WorkCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private List<ActionCategory.WoProcess> process(Business business, Date start, Date current) throws Exception {
		List<ActionCategory.WoProcess> list = new ArrayList<>();
		for (ApplicationStub applicationStub : ThisApplication.state.getApplicationStubs()) {
			for (ProcessStub stub : applicationStub.getProcessStubs()) {
				Long taskCount = 0L;
				Long taskExpiredCount = 0L;
				Long taskDuration = 0L;
				Long taskCompletedCount = 0L;
				Long taskCompletedExpiredCount = 0L;
				Long taskCompletedDuration = 0L;
				Long workCount = 0L;
				Long workExpiredCount = 0L;
				Long workDuration = 0L;
				Long workCompletedCount = 0L;
				Long workCompletedExpiredCount = 0L;
				Long workCompletedDuration = 0L;
				taskCount = this.countTask(business, start, stub);
				if (taskCount > 0) {
					taskExpiredCount = this.countTaskExpired(business, start, current, stub);
					taskDuration = this.durationTask(business, start, current, stub);
				}
				taskCompletedCount = this.countTaskCompleted(business, start, stub);
				if (taskCompletedCount > 0) {
					taskCompletedExpiredCount = this.countTaskCompletedExpired(business, start, stub);
					taskCompletedDuration = this.durationTaskCompleted(business, start, stub);
				}
				workCount = this.countWork(business, start, stub);
				if (workCount > 0) {
					workExpiredCount = this.countExpiredWork(business, start, current, stub);
					workDuration = this.durationWork(business, start, current, stub);
				}
				workCompletedCount = this.countWorkCompleted(business, start, stub);
				if (workCompletedCount > 0) {
					workCompletedExpiredCount = this.countExpiredWorkCompleted(business, start, stub);
					workCompletedDuration = this.durationWorkCompleted(business, start, stub);
				}
				ActionCategory.WoProcess wo = new ActionCategory.WoProcess();
				wo.setName(stub.getName());
				wo.setValue(stub.getValue());
				wo.setApplicationName(applicationStub.getName());
				wo.setApplicationValue(applicationStub.getValue());
				wo.setTaskCount(taskCount);
				wo.setTaskExpiredCount(taskExpiredCount);
				wo.setTaskDuration(taskDuration);
				wo.setTaskCompletedCount(taskCompletedCount);
				wo.setTaskCompletedExpiredCount(taskCompletedExpiredCount);
				wo.setTaskCompletedDuration(taskCompletedDuration);
				wo.setWorkCount(workCount);
				wo.setWorkExpiredCount(workExpiredCount);
				wo.setWorkDuration(workDuration);
				wo.setWorkCompletedCount(workCompletedCount);
				wo.setWorkCompletedExpiredCount(workCompletedExpiredCount);
				wo.setWorkCompletedDuration(workCompletedDuration);
				list.add(wo);
			}
		}
		list = list.stream().sorted(
				Comparator.comparing(ActionCategory.WoProcess::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	private Long countTask(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countTaskExpired(Business business, Date start, Date current, ProcessStub processStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Task_.expireTime), current));
		p = cb.and(p, cb.equal(root.get(Task_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationTask(Business business, Date start, Date current, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.process), processStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countTaskCompleted(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countTaskCompletedExpired(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationTaskCompleted(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countWork(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredWork(Business business, Date start, Date current, ProcessStub processStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Work_.expireTime), current));
		p = cb.and(p, cb.equal(root.get(Work_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWork(Business business, Date start, Date current, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.process), processStub.getValue()));
		cq.select(root.get(Work_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countWorkCompleted(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredWorkCompleted(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWorkCompleted(Business business, Date start, ProcessStub processStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processStub.getValue()));
		cq.select(cb.sum(root.get(WorkCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private List<ActionCategory.WoActivity> activity(Business business, Date start, Date current) throws Exception {
		List<ActionCategory.WoActivity> list = new ArrayList<>();
		for (ApplicationStub applicationStub : ThisApplication.state.getApplicationStubs()) {
			for (ProcessStub processStub : applicationStub.getProcessStubs()) {
				for (ActivityStub stub : processStub.getActivityStubs()) {
					Long taskCount = 0L;
					Long taskExpiredCount = 0L;
					Long taskDuration = 0L;
					Long taskCompletedCount = 0L;
					Long taskCompletedExpiredCount = 0L;
					Long taskCompletedDuration = 0L;
					Long workCount = 0L;
					Long workExpiredCount = 0L;
					Long workDuration = 0L;
					taskCount = this.countTask(business, start, stub);
					if (taskCount > 0) {
						taskExpiredCount = this.countTaskExpired(business, start, current, stub);
						taskDuration = this.durationTask(business, start, current, stub);
					}
					taskCompletedCount = this.countTaskCompleted(business, start, stub);
					if (taskCompletedCount > 0) {
						taskCompletedExpiredCount = this.countExpiredTaskCompleted(business, start, stub);
						taskCompletedDuration = this.durationTaskCompleted(business, start, stub);
					}
					workCount = this.countWork(business, start, stub);
					if (taskCompletedCount > 0) {
						workExpiredCount = this.countExpiredWork(business, start, current, stub);
						workDuration = this.durationWork(business, start, current, stub);
					}
					ActionCategory.WoActivity wo = new ActionCategory.WoActivity();
					/** 如果是开始或者结束,且所有数据为0,那么忽略数据 */
					if ((!Objects.equals(ActivityType.begin, stub.getActivityType()))
							&& (!Objects.equals(ActivityType.end, stub.getActivityType()))) {
						if (taskCount != 0L || taskCompletedCount != 0L || workCount != 0L) {
							wo.setName(stub.getName());
							wo.setValue(stub.getValue());
							wo.setApplicationName(applicationStub.getName());
							wo.setApplicationValue(applicationStub.getValue());
							wo.setProcessName(processStub.getName());
							wo.setProcessValue(processStub.getValue());
							wo.setTaskCount(taskCount);
							wo.setTaskExpiredCount(taskExpiredCount);
							wo.setTaskDuration(taskDuration);
							wo.setTaskCompletedCount(taskCompletedCount);
							wo.setTaskCompletedExpiredCount(taskCompletedExpiredCount);
							wo.setTaskCompletedDuration(taskCompletedDuration);
							wo.setWorkCount(workCount);
							wo.setWorkExpiredCount(workExpiredCount);
							wo.setWorkDuration(workDuration);
							list.add(wo);
						}
					}
				}
			}
		}
		list = list.stream().sorted(
				Comparator.comparing(ActionCategory.WoActivity::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	private Long countTask(Business business, Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countTaskExpired(Business business, Date start, Date current, ActivityStub activityStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Task_.expireTime), current));
		p = cb.and(p, cb.equal(root.get(Task_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationTask(Business business, Date start, Date current, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.activity), activityStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countTaskCompleted(Business business, Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredTaskCompleted(Business business, Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationTaskCompleted(Business business, Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityStub.getValue()));
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countWork(Business business, Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredWork(Business business, Date start, Date current, ActivityStub activityStub)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Work_.expireTime), current));
		p = cb.and(p, cb.equal(root.get(Work_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWork(Business business, Date start, Date current, ActivityStub activityStub) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.activity), activityStub.getValue()));
		cq.select(root.get(Work_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

}