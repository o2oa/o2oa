package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.ListTools;
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
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWo;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2Reset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

	/**
	 * 当前的待办
	 */
	private Task task;
	/**
	 * 添加的身份
	 */
	private List<String> identities;
	/**
	 * 当前的workLog,产生record必须
	 */
	private WorkLog workLog;
	/**
	 * 当前的work
	 */
	private Work work;
	/**
	 * work活动
	 */
	private Manual manual;
	/**
	 * 如果在待办身份矩阵中删除自己,那么先把自己的待办转为不参与流程的已办
	 */

	private final String series = StringTools.uniqueToken();
	private String taskCompletedId;
	private EffectivePerson effectivePerson;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		this.effectivePerson = effectivePerson;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			init(business, id, wi);
		}

		ManualTaskIdentityMatrix manualTaskIdentityMatrix = reset(this.task, wi.getKeep(), identities);

		if (StringUtils.isNotEmpty(wi.getOpinion()) || StringUtils.isNotEmpty(wi.getRouteName())) {
			updateTask(wi.getOpinion(), wi.getRouteName());
		}

		this.processingTask();

		this.processingWork();

		List<String> newTaskIds = new ArrayList<>();
		List<TaskCompleted> taskCompleteds = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			newTaskIds.addAll(emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, work.getJob(), Task.work_FIELDNAME,
					work.getId()));
			// 为办理的前的所有已办,用于在record中记录当前待办转为已办时的上一处理人
			taskCompleteds = emc.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
					task.getActivityToken());
		}
		Record rec = RecordBuilder.ofWorkProcessing(Record.TYPE_RESET, workLog, effectivePerson, manual, newTaskIds);
		rec.getProperties().setOpinion(wi.getOpinion());
		RecordBuilder.processing(rec);
		if (StringUtils.isNotEmpty(this.taskCompletedId)) {
			TaskCompletedBuilder.updateNextTaskIdentity(this.taskCompletedId,
					rec.getProperties().getNextManualTaskIdentityList(), task.getJob());
		}
		if (!taskCompleteds.isEmpty()) {
			TaskBuilder.updatePrevTaskIdentity(newTaskIds, taskCompleteds, this.task);
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(rec);
		wo.setManualTaskIdentityMatrix(manualTaskIdentityMatrix);
		result.setData(wo);
		return result;
	}

	private void init(Business business, String id, Wi wi) throws Exception {
		this.task = business.entityManagerContainer().find(id, Task.class);
		if (null == task) {
			throw new ExceptionEntityNotExist(id, Task.class);
		}
		this.workLog = business.entityManagerContainer().firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
				task.getJob(), WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
		if (null == workLog) {
			throw new ExceptionEntityNotExist(WorkLog.class);
		}
		this.work = business.entityManagerContainer().find(task.getWork(), Work.class);
		if (null == work) {
			throw new ExceptionEntityNotExist(id, Work.class);
		}
		Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowReset().build();
		if (BooleanUtils.isNotTrue(control.getAllowReset())) {
			throw new ExceptionAccessDenied(effectivePerson, work);
		}
		if (!work.getManualTaskIdentityMatrix().contains(task.getIdentity())) {
			throw new ExceptionIdentityNotInMatrix(task.getIdentity());
		}
		this.manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);

		this.identities = ListTools.trim(business.organization().identity().list(wi.getIdentityList()), true, true);

	}

	private void updateTask(String opinion, String routeName) throws Exception {
		V2EditWi req = new V2EditWi();
		req.setOpinion(opinion);
		req.setRouteName(routeName);
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", "v2", task.getId()), req, task.getJob()).getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionUpdateTask(task.getId());
		}
	}

	private void processingTask() throws Exception {
		ProcessingWi req = new ProcessingWi();
		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_RESET);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionProcessingTask(task.getId());
		} else {
			// 获得已办id
			this.taskCompletedId = resp.getId();
		}
	}

	private ManualTaskIdentityMatrix reset(Task task, boolean keep, List<String> identities) throws Exception {
		V2ResetWi req = new V2ResetWi();
		req.setIdentityList(identities);
		req.setKeep(keep);
		return ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v2", task.getId(), "reset"), req, task.getJob())
				.getData(V2AddManualTaskIdentityMatrixWo.class).getManualTaskIdentityMatrix();
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.V2Reset$Wi")
	public static class Wi extends V2ResetWi {

		private static final long serialVersionUID = 5747688678118966913L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.V2Reset$Wo")
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
