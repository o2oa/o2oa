package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

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
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;

public class V2Reset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

	private Task task;
	private List<TaskCompleted> taskCompleteds;
	private WorkLog workLog;
	private Work work;
	private final String series = StringTools.uniqueToken();
	private String taskCompletedId;
	private Wi wi;
	private EffectivePerson effectivePerson;
	private List<Task> newlyTasks;
	private Record rec;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		this.wi = this.convertToWrapIn(jsonElement, Wi.class);
		this.effectivePerson = effectivePerson;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			init(business, id);

			WoControl control = business.getControl(effectivePerson, this.task, WoControl.class);

			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, this.task);
			}
		}

		this.reset();

		if (BooleanUtils.isTrue(wi.getRemove())) {
			this.processingTask();
		}

		this.processingWork();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.newlyTasks = emc.listEqual(Task.class, Task.series_FIELDNAME, this.series);
		}

		rec = this.processingRecord(this.workLog, this.task, this.taskCompletedId, this.newlyTasks);

		if (StringUtils.isNotEmpty(this.taskCompletedId)) {
			this.updateNextTaskIdentity(this.taskCompletedId, rec.getProperties().getNextManualTaskIdentityList(),
					task.getJob());
		}

		if (!taskCompleteds.isEmpty()) {
			this.updatePrevTaskIdentity(
					ListTools.extractField(this.newlyTasks, JpaObject.id_FIELDNAME, String.class, true, true),
					taskCompleteds, this.task);
		}
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	private void init(Business business, String id) throws Exception {
		this.task = business.entityManagerContainer().find(id, Task.class);
		if (null == task) {
			throw new ExceptionEntityNotExist(id, Task.class);
		}
		this.workLog = business.entityManagerContainer().firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME,
				task.getJob(), WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());

		if (null == workLog) {
			throw new ExceptionEntityNotExist(WorkLog.class);
		}

		this.work = business.entityManagerContainer().find(task.getWork(), Work.class);

		if (null == work) {
			throw new ExceptionEntityNotExist(id, Work.class);
		}
		this.taskCompleteds = business.entityManagerContainer().listEqual(TaskCompleted.class,
				TaskCompleted.activityToken_FIELDNAME, task.getActivityToken());

	}

	private void processingTask() throws Exception {
		ProcessingWi req = new ProcessingWi();
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
		req.setAddBeforeList(this.wi.getAddBeforeList());
		req.setExtendList(this.wi.getExtendList());
		req.setAddAfterList(this.wi.getAddAfterList());
		req.setRemove(this.wi.getRemove());
		WrapBoolean resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v2", task.getId(), "reset"), req, task.getJob())
				.getData(WrapBoolean.class);
		if (BooleanUtils.isNotTrue(resp.getValue())) {
			throw new ExceptionReset(task.getId());
		}
	}

	private void processingWork() throws Exception {
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

	private Record processingRecord(WorkLog workLog, Task task, String taskCompletedId, List<Task> newlyTasks)
			throws Exception {
		Record r = RecordBuilder.ofTaskProcessing(Record.TYPE_RESET, workLog, task, taskCompletedId, newlyTasks);
		RecordBuilder.processing(rec);
		return r;
	}

	public static class Wi extends V2ResetWi {

		private static final long serialVersionUID = 5747688678118966913L;
	}

	public static class WoControl extends WorkControl {

		private static final long serialVersionUID = -6227098496393005824L;
	}

	public static class Wo extends Record {

		private static final long serialVersionUID = -4700549313374917582L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}