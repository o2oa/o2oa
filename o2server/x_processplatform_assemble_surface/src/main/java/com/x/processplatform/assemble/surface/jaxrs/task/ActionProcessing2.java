package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapAppend;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapProcessing;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity.PrevTask;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;

class ActionProcessing2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionProcessing2.class);

	private ActionResult<Wo> result = new ActionResult<>();

	private Wi wi;
	private Task task;
	private WorkLog workLog;
	private Work work;
	private String taskCompletedId;
	private String type;
	private EffectivePerson effectivePerson;
	private List<TaskCompleted> taskCompleteds = new ArrayList<>();
	private List<Task> newTasks = new ArrayList<>();

	private Record record;
	private String series = StringTools.uniqueToken();

	private static final String TYPE_APPENDTASK = "appendTask";
	private static final String TYPE_TASK = "task";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Audit audit = logger.audit(effectivePerson);
		this.effectivePerson = effectivePerson;
		wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.task = emc.find(id, Task.class);
			if (null == this.task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			// 获取当前环节已经完成的待办
			this.taskCompleteds = emc.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
					task.getActivityToken());
			this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob(),
					WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());

			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			this.work = emc.find(this.task.getWork(), Work.class);
			if (null == this.work) {
				throw new ExceptionEntityNotExist(this.task.getWork(), Work.class);
			}
			if ((!effectivePerson.isCipher()) && effectivePerson.isNotPerson(this.task.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, this.task);
			}
			if (StringUtils.isNotEmpty(wi.getRouteName()) || StringUtils.isNotEmpty(wi.getOpinion())
					|| (!StringUtils.equals(this.task.getMediaOpinion(), wi.getMediaOpinion()))) {
				emc.beginTransaction(Task.class);
				this.updateRouteNameOpinionMediaOpinion(task, wi);
				emc.commit();
			}
			this.type = this.type(business, task, wi);
		}
		this.processing();
		audit.log(null, "任务处理");
		Wo wo = Wo.copier.copy(record);
		result.setData(wo);
		return result;
	}

	private void updateRouteNameOpinionMediaOpinion(Task task, Wi wi) {
		/* 如果有输入新的路由决策覆盖原有决策 */
		if (StringUtils.isNotEmpty(wi.getRouteName())) {
			task.setRouteName(wi.getRouteName());
		}
		/* 如果有新的流程意见那么覆盖原有流程意见 */
		if (StringUtils.isNotEmpty(wi.getOpinion())) {
			task.setOpinion(wi.getOpinion());
		}
		/* 强制覆盖多媒体意见 */
		task.setMediaOpinion(wi.getMediaOpinion());
	}

	private String type(Business business, Task task, Wi wi) throws Exception {
		Manual manual = business.manual().pick(task.getActivity());
		if (null != manual) {
			Route route = null;
			for (Route o : business.route().pick(manual.getRouteList())) {
				if (StringUtils.equals(o.getName(), task.getRouteName())) {
					route = o;
					break;
				}
			}
			if ((null != route) && (StringUtils.equals(route.getType(), Route.TYPE_APPENDTASK))
					&& StringUtils.equals(manual.getId(), route.getActivity())) {
				return TYPE_APPENDTASK;
			}
		}
		return TYPE_TASK;
	}

	private void processing() throws Exception {
		switch (type) {
		case TYPE_APPENDTASK:
			this.processingAppendTask();
			break;
		default:
			this.processingTask();
			break;
		}
	}

	private void processingAppendTask() throws Exception {
		this.processingAppendTask_append();
		this.taskCompletedId = this.processing_processingTask(TaskCompleted.PROCESSINGTYPE_APPENDTASK);
		this.processing_processingWork(ProcessingAttributes.TYPE_APPENDTASK);
		this.processing_record(Record.TYPE_APPENDTASK);
		this.processing_updateTaskCompleted();
		this.processing_updateTask();
	}

	private void processingAppendTask_append() throws Exception {
		WrapAppend req = new WrapAppend();
		req.setIdentityList(this.wi.getAppendTaskIdentityList());
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", this.task.getId(), "append"), req, this.task.getJob())
				.getData(WrapStringList.class);
	}

	private void processingTask() throws Exception {
		this.taskCompletedId = this.processing_processingTask(TaskCompleted.PROCESSINGTYPE_TASK);
		this.processing_processingWork(ProcessingAttributes.TYPE_TASK);
		// 流程流转到取消环节，此时工作已被删除
		boolean flag = true;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, task.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, task.getJob()) == 0)) {
				flag = false;
			}
		}
		if (flag) {
			this.processing_record(Record.TYPE_TASK);
			this.processing_updateTaskCompleted();
			this.processing_updateTask();
		} else {
			record = new Record(workLog, task);
			record.setCompleted(true);
			record.setType(Record.TYPE_TASK);
		}
	}

	private String processing_processingTask(String processType) throws Exception {
		WrapProcessing req = new WrapProcessing();
		req.setProcessingType(processType);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionTaskProcessing(task.getId());
		} else {
			/* 获得已办id */
			return resp.getId();
		}
	}

	private void processing_processingWork(String type) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setIgnoreEmpowerIdentityList(wi.getIgnoreEmpowerIdentityList());
		req.setType(ProcessingAttributes.TYPE_TASK);
		req.setSeries(this.series);
		req.setPerson(task.getPerson());
		req.setIdentity(task.getIdentity());
		WoId resp = ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionWorkProcessing(task.getId());
		}
	}

	private void processing_record(String type) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			final List<String> nextTaskIdentities = new ArrayList<>();
			record = new Record(workLog, task);
			// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
			WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
					task.getJob());
			if (null != workCompleted) {
				record.setCompleted(true);
				record.setWorkCompleted(workCompleted.getId());
			}
			record.getProperties().setElapsed(
					Config.workTime().betweenMinutes(record.getProperties().getStartTime(), record.getRecordTime()));
			record.setType(type);
			List<Task> list = emc.fetchEqualAndEqual(Task.class,
					ListTools.toList(Task.person_FIELDNAME, Task.identity_FIELDNAME, Task.unit_FIELDNAME,
							Task.job_FIELDNAME, Task.work_FIELDNAME, Task.activity_FIELDNAME,
							Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME, Task.activityToken_FIELDNAME,
							Task.activityType_FIELDNAME, Task.empowerFromIdentity_FIELDNAME),
					Task.job_FIELDNAME, task.getJob(), Task.series_FIELDNAME, this.series);
			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
					.forEach(o -> {
						NextManual nextManual = new NextManual();
						nextManual.setActivity(o.getValue().get(0).getActivity());
						nextManual.setActivityAlias(o.getValue().get(0).getActivityAlias());
						nextManual.setActivityName(o.getValue().get(0).getActivityName());
						nextManual.setActivityToken(o.getValue().get(0).getActivityToken());
						nextManual.setActivityType(o.getValue().get(0).getActivityType());
						for (Task t : o.getValue()) {
							nextManual.getTaskIdentityList().add(t.getIdentity());
							this.newTasks.add(t);
							nextTaskIdentities.add(t.getIdentity());
						}
						record.getProperties().getNextManualList().add(nextManual);
					});
			// 去重
			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
			TaskCompleted taskCompleted = emc.find(taskCompletedId, TaskCompleted.class);
			if (null != taskCompleted) {
				// 处理完成后在重新写入待办信息
				record.getProperties().setOpinion(taskCompleted.getOpinion());
				record.getProperties().setRouteName(taskCompleted.getRouteName());
				record.getProperties().setMediaOpinion(taskCompleted.getMediaOpinion());
			}
		}
		WoId resp = ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", this.work.getJob()), record, this.task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionWorkProcessing(this.work.getId());
		}
	}

	private void processing_updateTaskCompleted() throws Exception {
		// 记录下一处理人信息
		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
		req.getTaskCompletedList().add(taskCompletedId);
		req.setNextTaskIdentityList(record.getProperties().getNextManualTaskIdentityList());
		ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, task.getJob())
				.getData(WrapBoolean.class);
	}

	private void processing_updateTask() throws Exception {
		// 记录上一处理人信息
		if (ListTools.isNotEmpty(newTasks)) {
			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
			req.setTaskList(ListTools.extractProperty(newTasks, JpaObject.id_FIELDNAME, String.class, true, true));
			this.taskCompleteds.stream().forEach(o -> {
				PrevTask prevTask = new PrevTask();
				prevTask.setCompletedTime(o.getCompletedTime());
				prevTask.setStartTime(o.getStartTime());
				prevTask.setOpinion(o.getOpinion());
				prevTask.setPerson(o.getPerson());
				prevTask.setIdentity(o.getIdentity());
				prevTask.setUnit(o.getUnit());
				prevTask.setRouteName(o.getRouteName());
				req.getPrevTaskIdentityList().add(prevTask.getIdentity());
				req.getPrevTaskList().add(prevTask);
			});
			PrevTask prevTask = new PrevTask();
			prevTask.setCompletedTime(new Date());
			prevTask.setStartTime(task.getStartTime());
			prevTask.setOpinion(task.getOpinion());
			prevTask.setPerson(task.getPerson());
			prevTask.setIdentity(task.getIdentity());
			prevTask.setUnit(task.getUnit());
			prevTask.setRouteName(task.getRouteName());
			req.getPrevTaskIdentityList().add(prevTask.getIdentity());
			req.setPrevTaskIdentity(prevTask.getIdentity());
			req.getPrevTaskList().add(prevTask);
			req.setPrevTask(prevTask);
			// 去重
			req.setPrevTaskIdentityList(ListTools.trim(req.getPrevTaskIdentityList(), true, true));
			ThisApplication.context().applications()
					.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
					.getData(WrapBoolean.class);
		}

		List<Task> empowerTasks = new ArrayList<>();
		for (Task o : newTasks) {
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
				WoId resp = ThisApplication.context().applications()
						.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
								Applications.joinQueryUri("record", "job", this.work.getJob()), r, this.task.getJob())
						.getData(WoId.class);
				if (StringUtils.isBlank(resp.getId())) {
					throw new ExceptionWorkProcessing(this.work.getId());
				}
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

	public static class Wo extends Record {

		private static final long serialVersionUID = -1771383649634969945L;
		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2702712453822143654L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Task.activity_FIELDNAME, Task.activityName_FIELDNAME,
						Task.person_FIELDNAME, Task.identity_FIELDNAME, Task.unit_FIELDNAME),
				null);

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("路由名称")
		private String routeName;

		@FieldDescribe("意见")
		private String opinion;

		@FieldDescribe("多媒体意见")
		private String mediaOpinion;

		@FieldDescribe("路由数据")
		private JsonElement routeData;

		@FieldDescribe("新添加的待办处理人")
		private List<String> appendTaskIdentityList;

		@FieldDescribe("忽略授权身份")
		private List<String> ignoreEmpowerIdentityList;

		public List<String> getAppendTaskIdentityList() {
			return appendTaskIdentityList;
		}

		public void setAppendTaskIdentityList(List<String> appendTaskIdentityList) {
			this.appendTaskIdentityList = appendTaskIdentityList;
		}

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getMediaOpinion() {
			return mediaOpinion;
		}

		public void setMediaOpinion(String mediaOpinion) {
			this.mediaOpinion = mediaOpinion;
		}

		public JsonElement getRouteData() {
			return routeData;
		}

		public void setRouteData(JsonElement routeData) {
			this.routeData = routeData;
		}

		public List<String> getIgnoreEmpowerIdentityList() {
			return ignoreEmpowerIdentityList;
		}

		public void setIgnoreEmpowerIdentityList(List<String> ignoreEmpowerIdentityList) {
			this.ignoreEmpowerIdentityList = ignoreEmpowerIdentityList;
		}
	}

}