package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.RecordBuilder;
import com.x.processplatform.assemble.surface.TaskBuilder;
import com.x.processplatform.assemble.surface.TaskCompletedBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionResetWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;

/**
 * tickets 加签
 */
public class ActionReset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Param param = this.init(effectivePerson, id, jsonElement);
		updateTask(param);
		reset(param);
		String taskCompletedId = this.processingTask(param);
		this.processingWork(param);
		List<String> newTaskIds = new ArrayList<>();
		List<TaskCompleted> taskCompleteds = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			newTaskIds.addAll(emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, param.getTask().getJob(),
					Task.work_FIELDNAME, param.getWork().getId()));
			// 为办理的前的所有已办,用于在record中记录当前待办转为已办时的上一处理人
			taskCompleteds = emc.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
					param.getTask().getActivityToken());
		}
		Record rec = RecordBuilder.ofWorkProcessing(Record.TYPE_RESET, param.getWorkLog(), effectivePerson,
				param.getManual(), newTaskIds);
		rec.getProperties().setOpinion(wi.getOpinion());
		RecordBuilder.processing(rec);
		if (StringUtils.isNotEmpty(taskCompletedId)) {
			TaskCompletedBuilder.updateNextTaskIdentity(taskCompletedId,
					rec.getProperties().getNextManualTaskIdentityList(), param.getTask().getJob());
		}
		if (!taskCompleteds.isEmpty()) {
			TaskBuilder.updatePrevTaskIdentity(newTaskIds, taskCompleteds, param.getTask());
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.setOpinion(wi.getOpinion());
		param.setRouteName(wi.getRouteName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Task task = business.entityManagerContainer().find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.setTask(task);
			Work work = business.entityManagerContainer().find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowReset().build();
			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			param.setWork(work);
			WorkLog workLog = business.entityManagerContainer().firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
					task.getJob(), WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.setWorkLog(workLog);
			Manual manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);
			param.setManual(manual);
			param.setDistinguishedNameList(
					business.organization().distinguishedName().list(wi.getDistinguishedNameList()));
			if (BooleanUtils.isTrue(wi.getKeep())) {
				param.getDistinguishedNameList().add(task.getDistinguishedName());
			}
			param.setDistinguishedNameList(
					param.getDistinguishedNameList().stream().distinct().collect(Collectors.toList()));
		}
		return param;
	}

	private void updateTask(Param param) throws Exception {
		if (StringUtils.isNotEmpty(param.getOpinion()) || StringUtils.isNotEmpty(param.getRouteName())) {
			V2EditWi req = new V2EditWi();
			req.setOpinion(param.getOpinion());
			req.setRouteName(param.getRouteName());
			WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("task", "v2", param.getTask().getId()), req, param.getTask().getJob())
					.getData(WoId.class);
			if (StringUtils.isEmpty(resp.getId())) {
				throw new ExceptionUpdateTask(param.getTask().getId());
			}
		}
	}

	private String processingTask(Param param) throws Exception {
		ProcessingWi req = new ProcessingWi();
		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_RESET);
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", param.getTask().getId(), "processing"), req, param.getTask().getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionProcessingTask(param.getTask().getId());
		} else {
			return resp.getId();
		}
	}

	private void reset(Param param) throws Exception {
		ActionResetWi req = new ActionResetWi();
		req.setDistinguishedNameList(param.getDistinguishedNameList());
		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", param.getTask().getId(), "reset"), req, param.getTask().getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.task.V3ResetWo.class);
	}

	private void processingWork(Param param) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_RESET);
		req.setSeries(param.getSeries());
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", param.getTask().getWork(), "processing"), req,
						param.getTask().getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionWorkProcessing(param.getWork().getId());
		}
	}

	public static class Param {

		private String series = StringTools.uniqueToken();

		private String opinion;

		private String routeName;

		private List<String> distinguishedNameList;

		private Task task;

		private Work work;

		private WorkLog workLog;

		private Manual manual;

		public Manual getManual() {
			return manual;
		}

		public void setManual(Manual manual) {
			this.manual = manual;
		}

		public String getSeries() {
			return series;
		}

		public void setSeries(String series) {
			this.series = series;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

		public Task getTask() {
			return task;
		}

		public void setTask(Task task) {
			this.task = task;
		}

		public Work getWork() {
			return work;
		}

		public void setWork(Work work) {
			this.work = work;
		}

		public WorkLog getWorkLog() {
			return workLog;
		}

		public void setWorkLog(WorkLog workLog) {
			this.workLog = workLog;
		}

	}

	public static class Wi extends ActionResetWi {

		private static final long serialVersionUID = 5747688678118966913L;

	}

	public static class Wo extends Record {

		private static final long serialVersionUID = -4700549313374917582L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

		public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
			this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
		}

		public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
			return manualTaskIdentityMatrix;
		}
	}

}
