//package com.x.processplatform.assemble.surface.jaxrs.work;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.apache.commons.collections4.ListUtils;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.project.Applications;
//import com.x.base.core.project.x_processplatform_service_processing;
//import com.x.base.core.project.bean.WrapCopier;
//import com.x.base.core.project.bean.WrapCopierFactory;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.exception.ExceptionAccessDenied;
//import com.x.base.core.project.exception.ExceptionEntityNotExist;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.base.core.project.jaxrs.WrapBoolean;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.ListTools;
//import com.x.base.core.project.tools.PropertyTools;
//import com.x.base.core.project.tools.StringTools;
//import com.x.processplatform.assemble.surface.Business;
//import com.x.processplatform.assemble.surface.ThisApplication;
//import com.x.processplatform.core.entity.content.Record;
//import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
//import com.x.processplatform.core.entity.content.Task;
//import com.x.processplatform.core.entity.content.TaskCompleted;
//import com.x.processplatform.core.entity.content.Work;
//import com.x.processplatform.core.entity.content.WorkLog;
//import com.x.processplatform.core.entity.element.Activity;
//import com.x.processplatform.core.entity.element.ActivityType;
//import com.x.processplatform.core.entity.element.Application;
//import com.x.processplatform.core.entity.element.Manual;
//import com.x.processplatform.core.entity.element.util.WorkLogTree;
//import com.x.processplatform.core.entity.element.util.WorkLogTree.Node;
//import com.x.processplatform.core.entity.element.util.WorkLogTree.Nodes;
//import com.x.processplatform.core.express.ProcessingAttributes;
//import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RetractWi;
//
//class V2ManageRetract extends BaseAction {
//
//	private static Logger logger = LoggerFactory.getLogger(V2ManageRetract.class);
//
//	private WorkLog workLog;
//	private TaskCompleted taskCompleted;
//	private Work work;
//	private String series = StringTools.uniqueToken();
//	private Record record;
//	private List<String> existTaskIds = new ArrayList<>();
//	private EffectivePerson effectivePerson;
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String person) throws Exception {
//
//		ActionResult<Wo> result = new ActionResult<>();
//
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			this.effectivePerson = effectivePerson;
//
//			String distinguishedName = business.organization().person().get(person);
//
//			work = emc.find(id, Work.class);
//			if (null == work) {
//				throw new ExceptionEntityNotExist(id, Work.class);
//			}
//
//			Application application = business.application().pick(work.getApplication());
//			if (null == application) {
//				throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
//			}
//
//			if (!business.application().allowControl(effectivePerson, application)) {
//				throw new ExceptionAccessDenied(effectivePerson, work);
//			}
//
//			Activity activity = business.getActivity(work);
//
//			if (null == activity) {
//				throw new ExceptionEntityNotExist(work.getActivity());
//			}
//
//			WorkLogTree workLogTree = new WorkLogTree(
//					emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob()));
//			/* 是否可以召回 */
//			if (PropertyTools.getOrElse(activity, Manual.allowRetract_FIELDNAME, Boolean.class, false)) {
//				Node node = workLogTree.location(work);
//				if (null != node) {
//					Nodes ups = node.upTo(ActivityType.manual, ActivityType.agent, ActivityType.choice,
//							ActivityType.delay, ActivityType.embed, ActivityType.invoke, ActivityType.parallel,
//							ActivityType.split, ActivityType.publish);
//					for (Node o : ups) {
//						if (business.entityManagerContainer().countEqualAndEqual(TaskCompleted.class,
//								TaskCompleted.person_FIELDNAME, distinguishedName,
//								TaskCompleted.activityToken_FIELDNAME, o.getWorkLog().getFromActivityToken()) > 0) {
//							workLog = o.getWorkLog();
//							break;
//						}
//					}
//				}
//			}
//
//			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
//
//			if (null == workLog) {
//				throw new ExceptionRetractNoWorkLog(work.getId());
//			}
//
//			if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob(),
//					TaskCompleted.activityToken_FIELDNAME, work.getActivityToken()) > 0) {
//				throw new ExceptionRetractNoneTaskCompleted(work.getTitle(), work.getId());
//			}
//
//			taskCompleted = findLastTaskCompleted(business);
//			if (null == taskCompleted) {
//				throw new ExceptionNoTaskCompletedToRetract(workLog.getId(), distinguishedName);
//			}
//
//		}
//
//		this.retract();
//
//		this.processing();
//
//		this.record();
//
//		result.setData(Wo.copier.copy(record));
//
//		return result;
//
//	}
//
//	private TaskCompleted findLastTaskCompleted(Business business) throws Exception {
//		List<TaskCompleted> list = business.entityManagerContainer().listEqualAndEqualAndEqual(TaskCompleted.class,
//				TaskCompleted.job_FIELDNAME, workLog.getJob(), TaskCompleted.activityToken_FIELDNAME,
//				workLog.getFromActivityToken(), TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
//		return list.stream().sorted(Comparator.comparing(TaskCompleted::getStartTime).reversed()).findFirst()
//				.orElse(null);
//	}
//
//	private void retract() throws Exception {
//		Req req = new Req();
//		req.setTaskCompleted(taskCompleted.getId());
//		req.setWorkLog(workLog.getId());
//		WrapBoolean resp = ThisApplication.context().applications()
//				.putQuery(x_processplatform_service_processing.class,
//						Applications.joinQueryUri("work", "v2", this.work.getId(), "retract"), req, work.getJob())
//				.getData(WrapBoolean.class);
//		if (BooleanUtils.isNotTrue(resp.getValue())) {
//			throw new ExceptionRetract(this.work.getId());
//		}
//	}
//
//	private void processing() throws Exception {
//		ProcessingAttributes req = new ProcessingAttributes();
//		req.setType(ProcessingAttributes.TYPE_RETRACT);
//		req.setSeries(series);
//		WoId resp = ThisApplication.context().applications()
//				.putQuery(x_processplatform_service_processing.class,
//						Applications.joinQueryUri("work", this.work.getId(), "processing"), req, work.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isBlank(resp.getId())) {
//			throw new ExceptionRetract(this.work.getId());
//		}
//	}
//
//	private void record() throws Exception {
//		record = new Record(workLog);
//		record.setType(Record.TYPE_RETRACT);
//		record.getProperties().setElapsed(
//				Config.workTime().betweenMinutes(record.getProperties().getStartTime(), record.getRecordTime()));
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			work = emc.find(work.getId(), Work.class);
//			if (null != work) {
//				record.setArrivedActivity(work.getActivity());
//				record.setArrivedActivityType(work.getActivityType());
//				record.setArrivedActivityToken(work.getActivityToken());
//				record.setArrivedActivityName(work.getActivityName());
//				record.setArrivedActivityAlias(work.getActivityAlias());
//				record.setIdentity(taskCompleted.getIdentity());
//				record.setPerson(taskCompleted.getPerson());
//				record.setUnit(taskCompleted.getUnit());
//				final List<String> nextTaskIdentities = new ArrayList<>();
//				List<String> ids = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
//				ids = ListUtils.subtract(ids, existTaskIds);
//				List<Task> list = emc.fetch(ids, Task.class,
//						ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
//								Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
//								Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
//				list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
//						.forEach(o -> {
//							Task task = o.getValue().get(0);
//							NextManual nextManual = new NextManual();
//							nextManual.setActivity(task.getActivity());
//							nextManual.setActivityAlias(task.getActivityAlias());
//							nextManual.setActivityName(task.getActivityName());
//							nextManual.setActivityToken(task.getActivityToken());
//							nextManual.setActivityType(task.getActivityType());
//							for (Task t : o.getValue()) {
//								nextManual.getTaskIdentityList().add(t.getIdentity());
//								nextTaskIdentities.add(t.getIdentity());
//							}
//							record.getProperties().getNextManualList().add(nextManual);
//						});
//				/* 去重 */
//				record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
//			}
//		}
//		WoId resp = ThisApplication.context().applications()
//				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("record", "job", work.getJob()), record, this.work.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isBlank(resp.getId())) {
//			throw new ExceptionRetract(this.work.getId());
//		}
//	}
//
//	public static class Req extends V2RetractWi {
//
//		private static final long serialVersionUID = 5007158527128459266L;
//
//	}
//
//	public static class Wo extends Record {
//
//		private static final long serialVersionUID = -5007785846454720742L;
//
//		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
//				JpaObject.FieldsInvisible);
//	}
//
//}
