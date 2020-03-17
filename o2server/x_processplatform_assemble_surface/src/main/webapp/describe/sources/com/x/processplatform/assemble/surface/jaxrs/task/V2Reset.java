package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapProcessing;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;

public class V2Reset extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Reset.class);

	private Task task;
	private WorkLog workLog;
	private Work work;
	private final String series = StringTools.uniqueToken();
	private List<String> identites = new ArrayList<>();
	private List<String> newTasks = new ArrayList<>();
	private String taskCompletedId;
	private List<String> existTaskIds = new ArrayList<>();
	private Wi wi;
	private Record record;
	private EffectivePerson effectivePerson;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		this.wi = this.convertToWrapIn(jsonElement, Wi.class);
		this.effectivePerson = effectivePerson;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);

			this.task = emc.find(id, Task.class);

			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}

			this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob(),
					WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());

			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}

			this.work = emc.find(task.getWork(), Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			WoControl control = business.getControl(effectivePerson, task, WoControl.class);

			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}

			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
			/* 检查reset人员 */
			identites = business.organization().identity().list(wi.getIdentityList());

			/* 在新增待办人员中删除当前的处理人 */
			identites = ListUtils.subtract(identites, ListTools.toList(task.getIdentity()));

			if (ListTools.isEmpty(identites)) {
				throw new ExceptionIdentityEmpty();
			}

			if (StringUtils.isNotEmpty(wi.getRouteName()) || StringUtils.isNotEmpty(wi.getOpinion())) {
				emc.beginTransaction(Task.class);
				/* 如果有选择新的路由那么覆盖之前的选择 */
				if (StringUtils.isNotEmpty(wi.getRouteName())) {
					task.setRouteName(wi.getRouteName());
				}
				/* 如果有新的流程意见那么覆盖流程意见 */
				if (StringUtils.isNotEmpty(wi.getOpinion())) {
					task.setOpinion(wi.getOpinion());
				}
				emc.commit();
			}
		}

		this.reset();

		if (!wi.getKeep()) {
			this.processingTask();
		}

		this.processing();

		this.record();

		if (StringUtils.isNotEmpty(this.taskCompletedId)) {
			this.updateTaskCompleted();
		}

		this.updateTask();

		Wo wo = Wo.copier.copy(record);
		result.setData(wo);
		return result;
	}

	private void processingTask() throws Exception {
		WrapProcessing req = new WrapProcessing();
		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_RESET);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionTaskProcessing(task.getId());
		} else {
			/* 获得已办id */
			this.taskCompletedId = resp.getId();
		}
	}

	private void reset() throws Exception {
		V2ResetWi req = new V2ResetWi();
		req.setIdentityList(identites);
		req.setKeep(BooleanUtils.isTrue(wi.getKeep()));
		WrapBoolean resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v2", task.getId(), "reset"), req, task.getJob())
				.getData(WrapBoolean.class);

		if (!resp.getValue()) {
			throw new ExceptionReset(task.getId());
		}
	}

	private void processing() throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_RESET);
		req.setSeries(this.series);
		WoId resp = ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionWorkProcessing(work.getId());
		}
	}

	private void record() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			final List<String> nextTaskIdentities = new ArrayList<>();
			this.record = new Record(workLog, task);
			record.setPerson(effectivePerson.getDistinguishedName());
			record.setType(Record.TYPE_RESET);
			List<String> ids = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
			ids = ListUtils.subtract(ids, existTaskIds);
			List<Task> list = emc.fetch(ids, Task.class,
					ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
							Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
							Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
			if (wi.getKeep()) {
				/* 不排除自己,那么把自己再加进去 */
				list.add(task);
			}
			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
					.forEach(o -> {
						Task task = o.getValue().get(0);
						NextManual nextManual = new NextManual();
						nextManual.setActivity(task.getActivity());
						nextManual.setActivityAlias(task.getActivityAlias());
						nextManual.setActivityName(task.getActivityName());
						nextManual.setActivityToken(task.getActivityToken());
						nextManual.setActivityType(task.getActivityType());
						for (Task t : o.getValue()) {
							nextManual.getTaskIdentityList().add(t.getIdentity());
							nextTaskIdentities.add(t.getIdentity());
						}
						record.getProperties().getNextManualList().add(nextManual);
					});
			/* 去重 */
			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
		}
		WoId resp = ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", work.getJob()), record, this.work.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionReset(this.task.getId());
		}
	}

	private void updateTaskCompleted() throws Exception {
		/* 记录下一处理人信息 */
		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
		req.getTaskCompletedList().add(this.taskCompletedId);
		req.setNextTaskIdentityList(record.getProperties().getNextManualTaskIdentityList());
		ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, task.getJob())
				.getData(WrapBoolean.class);
	}

	private void updateTask() throws Exception {
		/* 记录上一处理人信息 */
		if (ListTools.isNotEmpty(newTasks)) {
			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
			req.setTaskList(newTasks);
			req.getPrevTaskIdentityList().add(task.getIdentity());
			ThisApplication.context().applications()
					.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
					.getData(WrapBoolean.class);
		}
	}

	public static class Wi extends V2ResetWi {
	}

	public static class WoControl extends WorkControl {
	}

	public static class Wo extends Record {

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}