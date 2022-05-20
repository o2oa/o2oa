//package com.x.processplatform.assemble.surface.jaxrs.work;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.apache.commons.collections4.ListUtils;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.gson.JsonElement;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.project.Applications;
//import com.x.base.core.project.x_processplatform_service_processing;
//import com.x.base.core.project.exception.ExceptionAccessDenied;
//import com.x.base.core.project.exception.ExceptionEntityNotExist;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.base.core.project.jaxrs.WrapBoolean;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.ListTools;
//import com.x.base.core.project.tools.StringTools;
//import com.x.processplatform.assemble.surface.Business;
//import com.x.processplatform.assemble.surface.ThisApplication;
//import com.x.processplatform.assemble.surface.WorkControl;
//import com.x.processplatform.core.entity.content.Record;
//import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
//import com.x.processplatform.core.entity.content.Task;
//import com.x.processplatform.core.entity.content.TaskCompleted;
//import com.x.processplatform.core.entity.content.Work;
//import com.x.processplatform.core.entity.content.WorkCompleted;
//import com.x.processplatform.core.entity.content.WorkLog;
//import com.x.processplatform.core.express.ProcessingAttributes;
//import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
//import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ExtendWi;
//import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
//import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;
//
//public class V2ExtendTask extends BaseAction {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(V2ExtendTask.class);
//	// 当前提交的串号
//	private final String series = StringTools.uniqueToken();
//	// 新加入的身份列表
//	private List<String> identites = new ArrayList<>();
//	// 新创建的待办标识列表
//	private List<String> newTasks = new ArrayList<>();
//	// 当前待办转成已办得到的已办id
//	private String taskCompletedId;
//	// 已经存在的待办标识列表
//	private List<String> existTaskIds = new ArrayList<>();
//	// 输入
//	private Wi wi;
//	// 当前执行用户
//	private EffectivePerson effectivePerson;
//	// 根据输入得到的待办
//	private Task task = null;
//	// 当前待办的workLog
//	private WorkLog workLog = null;
//	// 本环节创建的record
//	private Record concreteRecord = null;
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
//		}
//		this.init(effectivePerson, jsonElement);
//		this.extend(this.task, wi.getReplace(), identites);
//		if (BooleanUtils.isTrue(wi.getReplace())) {
//			taskCompletedId = this.processingTask(this.task);
//		}
//		this.processingWork(this.task);
//		this.createRecord(task, workLog);
//		if (StringUtils.isNotEmpty(taskCompletedId)) {
//			this.updateTaskCompleted();
//		}
//		this.updateTask();
//		return result();
//	}
//
//	private void init(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			this.effectivePerson = effectivePerson;
//			this.wi = this.convertToWrapIn(jsonElement, Wi.class);
//			this.task = emc.find(wi.getTask(), Task.class);
//			if (null == task) {
//				throw new ExceptionEntityNotExist(wi.getTask(), Task.class);
//			}
//			if (emc.countEqual(Work.class, JpaObject.id_FIELDNAME, task.getWork()) < 1) {
//				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
//			}
//			this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob(),
//					WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());
//			if (null == workLog) {
//				throw new ExceptionEntityNotExist(WorkLog.class);
//			}
//			WoControl control = business.getControl(effectivePerson, task, WoControl.class);
//			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
//				throw new ExceptionAccessDenied(effectivePerson, task);
//			}
//			this.existTaskIds = emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, task.getJob(), Task.work_FIELDNAME,
//					task.getWork());
//			this.identites = business.organization().identity().list(wi.getIdentityList());
//			// 在新扩充待办人员中去除已经有待办人员
//			identites.remove(task.getIdentity());
//			if (ListTools.isEmpty(identites)) {
//				throw new ExceptionIdentityEmpty();
//			}
//		}
//	}
//
//	private void extend(Task task, Boolean replace, List<String> identites) throws Exception {
//		V2ExtendWi req = new V2ExtendWi();
//		req.setTask(task.getId());
//		req.setReplace(replace);
//		req.setIdentityList(identites);
//		WrapBoolean resp = ThisApplication.context().applications()
//				.postQuery(x_processplatform_service_processing.class,
//						Applications.joinQueryUri("task", "v2", "extend"), req, task.getJob())
//				.getData(WrapBoolean.class);
//		if (BooleanUtils.isNotTrue(resp.getValue())) {
//			throw new ExceptionExtend(task.getId());
//		}
//	}
//
//	private String processingTask(Task task) throws Exception {
//		ProcessingWi req = new ProcessingWi();
//		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_EXTEND);
//		WoId resp = ThisApplication.context().applications()
//				.putQuery(x_processplatform_service_processing.class,
//						Applications.joinQueryUri("task", task.getId(), "processing"), req, task.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isEmpty(resp.getId())) {
//			throw new ExceptionTaskProcessing(task.getId());
//		} else {
//			return resp.getId();
//		}
//	}
//
//	private void processingWork(Task task) throws Exception {
//		ProcessingAttributes req = new ProcessingAttributes();
//		req.setType(ProcessingAttributes.TYPE_TASKEXTEND);
//		req.setSeries(this.series);
//		WoId resp = ThisApplication.context().applications()
//				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isEmpty(resp.getId())) {
//			throw new ExceptionWorkProcessing(task.getWork());
//		}
//	}
//
//	private void createRecord(Task task, WorkLog workLog) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			concreteRecord = new Record(workLog, task);
//			// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
//			WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
//					task.getJob());
//			if (null != workCompleted) {
//				concreteRecord.setCompleted(true);
//				concreteRecord.setWorkCompleted(workCompleted.getId());
//			}
//			concreteRecord.setPerson(effectivePerson.getDistinguishedName());
//			concreteRecord.setType(Record.TYPE_TASKEXTEND);
//			createRecordAdjust(business, task, concreteRecord);
//		}
//		WoId resp = ThisApplication.context().applications()
//				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("record", "job", task.getJob()), concreteRecord, task.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isBlank(resp.getId())) {
//			throw new ExceptionExtend(task.getId());
//		}
//	}
//
//	private void createRecordAdjust(Business business, Task task, Record concreteRecord) throws Exception {
//		List<String> ids = business.entityManagerContainer().idsEqualAndEqual(Task.class, Task.job_FIELDNAME,
//				task.getJob(), Task.work_FIELDNAME, task.getWork());
//		ids = ListUtils.subtract(ids, existTaskIds);
//		List<Task> list = business.entityManagerContainer().fetch(ids, Task.class,
//				ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
//						Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
//						Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
//		final List<String> nextTaskIdentities = new ArrayList<>();
//		list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
//				.forEach(o -> {
//					Task next = o.getValue().get(0);
//					NextManual nextManual = new NextManual();
//					nextManual.setActivity(next.getActivity());
//					nextManual.setActivityAlias(next.getActivityAlias());
//					nextManual.setActivityName(next.getActivityName());
//					nextManual.setActivityToken(next.getActivityToken());
//					nextManual.setActivityType(next.getActivityType());
//					for (Task t : o.getValue()) {
//						nextManual.getTaskIdentityList().add(t.getIdentity());
//						nextTaskIdentities.add(t.getIdentity());
//					}
//					concreteRecord.getProperties().getNextManualList().add(nextManual);
//				});
//		// 去重
//		concreteRecord.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
//	}
//
//	private void updateTaskCompleted() throws Exception {
//		// 记录下一处理人信息
//		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
//		req.getTaskCompletedList().add(this.taskCompletedId);
//		req.setNextTaskIdentityList(concreteRecord.getProperties().getNextManualTaskIdentityList());
//		ThisApplication.context().applications()
//				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, task.getJob())
//				.getData(WrapBoolean.class);
//	}
//
//	private void updateTask() throws Exception {
//		// 记录上一处理人信息
//		if (ListTools.isNotEmpty(newTasks)) {
//			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
//			req.setTaskList(newTasks);
//			req.setPrevTaskIdentity(task.getIdentity());
//			req.getPrevTaskIdentityList().add(task.getIdentity());
//			ThisApplication.context().applications()
//					.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
//					.getData(WrapBoolean.class);
//		}
//	}
//
//	private ActionResult<Wo> result() {
//		ActionResult<Wo> result = new ActionResult<>();
//		Wo wo = new Wo();
//		wo.setValue(true);
//		result.setData(wo);
//		return result;
//	}
//
//	public static class Wi extends V2ExtendWi {
//
//		private static final long serialVersionUID = -3241215869441470402L;
//
//	}
//
//	public static class WoControl extends WorkControl {
//
//		private static final long serialVersionUID = -8781558581462660831L;
//	}
//
//	public static class Wo extends WrapBoolean {
//
//		private static final long serialVersionUID = 4883624438858385234L;
//
//	}
//
//}