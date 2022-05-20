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
//import com.x.base.core.project.Applications;
//import com.x.base.core.project.x_processplatform_service_processing;
//import com.x.base.core.project.exception.ExceptionEntityExist;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.base.core.project.jaxrs.WrapBoolean;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.ListTools;
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
//import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2AddTaskWi;
//import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
//import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
//import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;
//
//public class V2AddTask extends BaseAction {
//
//	private static Logger LOGGER = LoggerFactory.getLogger(V2AddTask.class);
//
//	// 工作
//	private Work work;
//
//	// 添加的人员
//	private List<String> identities;
//
//	// 用于定位的身份
//	private String identity;
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
//		}
//		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//		this.init(effectivePerson, wi);
//		this.addTask(this.task, wi.getAfter(), wi.getReplace(), identites);
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
//	private void init(EffectivePerson effectivePerson, Wi wi) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			this.work = emc.find(wi.getWork(), Work.class);
//			if (null == work) {
//				throw new ExceptionEntityExist(wi.getWork(), Work.class);
//			}
//			identities = business.organization().identity().list(wi.getIdentityList());
//			if (identities.isEmpty()) {
//				throw new ExceptionEmptyIdentity(wi.getIdentityList());
//			}
//			identity = business.organization().identity().get(wi.getIdentity());
//		}
//	}
//
//	private void addTask(Work work, String identity, List<String> identities, boolean after, boolean replace)
//			throws Exception {
//		V2AddTaskWi req = new V2AddTaskWi();
//		req.setWork(work.getId());
//		req.setIdentity(wi.getIdentity());
//		req.setAfter(wi.getAfter());
//		req.setReplace(wi.getReplace());
//		req.setIdentityList(wi.getIdentityList());
//		WrapBoolean resp = ThisApplication.context().applications()
//				.postQuery(x_processplatform_service_processing.class, Applications.joinQueryUri("task", "v2", "add"),
//						req, task.getJob())
//				.getData(WrapBoolean.class);
//		if (BooleanUtils.isNotTrue(resp.getValue())) {
//			throw new ExceptionAdd(task.getId());
//		}
//	}
//
//	private String processingTask(Task task) throws Exception {
//		ProcessingWi req = new ProcessingWi();
//		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_ADD);
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
//		req.setType(ProcessingAttributes.TYPE_TASKADD);
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
//			concreteRecord.setType(Record.TYPE_TASKADD);
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
//	public static class Wi extends V2AddTaskWi {
//
//		private static final long serialVersionUID = -6251874269093504136L;
//
//	}
//
//	public static class WoControl extends WorkControl {
//
//		private static final long serialVersionUID = -8675239528577375846L;
//
//	}
//
//	public static class Wo extends WrapBoolean {
//
//		private static final long serialVersionUID = 8155067200427920853L;
//
//	}
//
//}