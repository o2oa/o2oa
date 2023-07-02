package com.x.processplatform.service.processing.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;

public class PassExpired extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(PassExpired.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String sequence = null;
			List<Task> targets = new ArrayList<>();
			Map<String, Route> manualToRoute = null;
			AtomicInteger count = new AtomicInteger(0);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				manualToRoute = this.linkPassExpiredManualToRoute(emc);
			}
			if (!manualToRoute.isEmpty()) {
				do {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						targets = this.list(emc, sequence, manualToRoute);
					}
					if (!targets.isEmpty()) {
						sequence = targets.get(targets.size() - 1).getSequence();
						for (Task task : targets) {
							logger.print("执行超时工作默认路由流转:{}, id:{}.", task.getTitle(), task.getId());
							this.executeWithLogException(task);
							count.incrementAndGet();
						}
					}
				} while (!targets.isEmpty());
				logger.print("完成{}个超时工作默认路由流转, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void executeWithLogException(Task task) {
		try {
			this.execute(task);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private void execute(Task task) throws Exception {
		try {
			String series = StringTools.uniqueToken();
			WorkLog workLog = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
						WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			}
			this.passExpired(task);
			String taskCompletedId = this.porcessingTask(task);
			this.porcessingWork(task, series);
			List<Task> newTasks = new ArrayList<>();
			Record record = this.record(workLog, task, taskCompletedId, series, newTasks);
			this.updateTask(task, newTasks);
			this.updateTaskCompleted(taskCompletedId, record);
		} catch (Exception e) {
			throw new ExceptionPassExpired(e, task.getId(), task.getTitle());
		}
	}

	private void passExpired(Task task) throws Exception {
		WrapBoolean respOfPassExpired = ThisApplication.context().applications()
				.getQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "pass", "expired"), task.getJob())
				.getData(WrapBoolean.class);
		if (BooleanUtils.isNotTrue(respOfPassExpired.getValue())) {
			throw new ExceptionInvokePassExpired(task.getId());
		}
	}

	private String porcessingTask(Task task) throws Exception {
		ProcessingWi req = new ProcessingWi();
		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_PASSEXPIRED);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionInvokeProcessingTask(task.getId());
		} else {
			return resp.getId();
		}
	}

	private void porcessingWork(Task task, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_TASK);
		req.setSeries(series);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionInvokeProcessingWork(task.getId());
		}
	}

	private Record record(WorkLog workLog, Task task, String taskCompletedId, String series, List<Task> newTasks)
			throws Exception {
		Record record = new Record(workLog, task);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			final List<String> nextTaskIdentities = new ArrayList<>();
			record.getProperties().setElapsed(
					Config.workTime().betweenMinutes(record.getProperties().getStartTime(), record.getRecordTime()));
			record.setType(Record.TYPE_PASSEXPIRED);
			List<Task> list = emc.fetchEqualAndEqual(Task.class,
					ListTools.toList(Task.person_FIELDNAME, Task.identity_FIELDNAME, Task.unit_FIELDNAME,
							Task.job_FIELDNAME, Task.work_FIELDNAME, Task.activity_FIELDNAME,
							Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME, Task.activityToken_FIELDNAME,
							Task.activityType_FIELDNAME, Task.empowerFromIdentity_FIELDNAME),
					Task.job_FIELDNAME, task.getJob(), Task.series_FIELDNAME, series);
			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
					.forEach(o -> {
						Task t = o.getValue().get(0);
						NextManual nextManual = new NextManual();
						nextManual.setActivity(t.getActivity());
						nextManual.setActivityAlias(t.getActivityAlias());
						nextManual.setActivityName(t.getActivityName());
						nextManual.setActivityToken(t.getActivityToken());
						nextManual.setActivityType(t.getActivityType());
						for (Task obj : o.getValue()) {
							nextManual.getTaskIdentityList().add(obj.getIdentity());
							newTasks.add(obj);
							nextTaskIdentities.add(obj.getIdentity());
						}
						record.getProperties().getNextManualList().add(nextManual);
					});
			/* 去重 */
			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
			TaskCompleted taskCompleted = emc.find(taskCompletedId, TaskCompleted.class);
			if (null != taskCompleted) {
				/* 处理完成后在重新写入待办信息 */
				record.getProperties().setOpinion(taskCompleted.getOpinion());
				record.getProperties().setRouteName(taskCompleted.getRouteName());
				record.getProperties().setMediaOpinion(taskCompleted.getMediaOpinion());
			}
		}
		WoId resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", task.getJob()), record, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionInvokeCreateRecord(record);
		}
		return record;
	}

	private void updateTaskCompleted(String taskCompletedId, Record record) throws Exception {
		/* 记录下一处理人信息 */
		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
		req.getTaskCompletedList().add(taskCompletedId);
		req.setNextTaskIdentityList(record.getProperties().getNextManualTaskIdentityList());
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, record.getJob())
				.getData(WrapBoolean.class);
	}

	private void updateTask(Task task, List<Task> list) throws Exception {
		/* 记录上一处理人信息 */
		if (ListTools.isNotEmpty(list)) {
			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
			req.setTaskList(ListTools.extractProperty(list, JpaObject.id_FIELDNAME, String.class, true, true));
			req.getPrevTaskIdentityList().add(task.getIdentity());
			ThisApplication.context().applications()
					.putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
					.getData(WrapBoolean.class);
		}
		List<Task> empowerTasks = new ArrayList<>();
		for (Task o : list) {
			if (StringUtils.isNotEmpty(o.getEmpowerFromIdentity())
					&& (!StringUtils.equals(o.getEmpowerFromIdentity(), o.getIdentity()))) {
				empowerTasks.add(o);
			}
		}
		if (!empowerTasks.isEmpty()) {
			List<Record> empowerRecords = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				for (Task o : empowerTasks) {
					empowerRecords.add(createEmpowerRecord(business, o));
				}
			}
			for (Record r : empowerRecords) {
				ThisApplication.context().applications()
						.postQuery(x_processplatform_service_processing.class,
								Applications.joinQueryUri("record", "job", task.getJob()), r, task.getJob())
						.getData(WoId.class);
			}
		}
	}

	private Record createEmpowerRecord(Business business, Task task) throws Exception {
		Record o = new Record();
		o.setType(Record.TYPE_EMPOWER);
		o.setApplication(task.getApplication());
		o.setProcess(task.getProcess());
		o.setJob(task.getJob());
		o.setCompleted(false);
		o.setWork(task.getWork());
		o.setFromActivity(task.getActivity());
		o.setFromActivityAlias(task.getActivityAlias());
		o.setFromActivityName(task.getActivityName());
		o.setFromActivityToken(task.getActivityToken());
		o.setFromActivityType(task.getActivityType());
		o.setArrivedActivity(task.getActivity());
		o.setArrivedActivityAlias(task.getActivityAlias());
		o.setArrivedActivityName(task.getActivityName());
		o.setArrivedActivityToken(task.getActivityToken());
		o.setArrivedActivityType(task.getActivityType());
		o.getProperties().setEmpowerToPerson(task.getPerson());
		o.getProperties().setEmpowerToIdentity(task.getIdentity());
		o.getProperties().setEmpowerToUnit(task.getUnit());
		o.setIdentity(task.getEmpowerFromIdentity());
		o.setPerson(business.organization().person().getWithIdentity(o.getIdentity()));
		o.setUnit(business.organization().unit().getWithIdentity(o.getIdentity()));
		o.getProperties().setElapsed(0L);
		NextManual nextManual = new NextManual();
		nextManual.setActivity(task.getActivity());
		nextManual.setActivityAlias(task.getActivityAlias());
		nextManual.setActivityName(task.getActivityName());
		nextManual.setActivityToken(task.getActivityToken());
		nextManual.setActivityType(task.getActivityType());
		o.getProperties().getNextManualList().add(nextManual);
		o.getProperties().getNextManualTaskIdentityList().add(task.getIdentity());
		return o;
	}

	private List<Task> list(EntityManagerContainer emc, String sequence, Map<String, Route> manualToRoute)
			throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.expired), true);
		p = cb.and(p, root.get(Task_.activity).in(manualToRoute.keySet()));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(root.get(JpaObject_.sequence), sequence));
		}
		return em.createQuery(cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject_.sequence))))
				.setMaxResults(200).getResultList();
	}

	private Map<String, Route> linkPassExpiredManualToRoute(EntityManagerContainer emc) throws Exception {
		List<Route> routes = emc.fetchEqual(Route.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Route.name_FIELDNAME, Route.opinion_FIELDNAME),
				Route.passExpired_FIELDNAME, true);
		List<Manual> manuals = emc.fetchIn(Manual.class, Manual.routeList_FIELDNAME,
				ListTools.extractProperty(routes, JpaObject.id_FIELDNAME, String.class, true, true));
		List<String> ids = new ArrayList<>();
		for (Manual m : manuals) {
			ids.add(m.getId());
		}
		ids = ListTools.trim(ids, true, true);
		Map<String, Route> map = new HashMap<>();
		ids.stream().forEach(m -> {
			for (Route r : routes) {
				map.put(m, r);
			}
		});
		return map;
	}

}