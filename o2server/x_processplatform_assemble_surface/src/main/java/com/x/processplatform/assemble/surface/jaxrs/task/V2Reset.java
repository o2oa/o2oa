package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
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
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualMode;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2AddManualTaskIdentityMatrixWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;

public class V2Reset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

	// 当前的待办
	private Task task;
	// 添加的身份
	private List<String> identities;
	// 当前的workLog,产生record必须
	private WorkLog workLog;
	// 当前的work
	private Work work;
	// work活动
	private Manual manual;
	// 如果在待办身份矩阵中删除自己,那么先把自己的待办转为不参与流程的已办
	private List<TaskCompleted> taskCompleteds;
	private final String series = StringTools.uniqueToken();
	private String taskCompletedId;
	private Wi wi;
	private EffectivePerson effectivePerson;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		this.wi = this.convertToWrapIn(jsonElement, Wi.class);
		this.effectivePerson = effectivePerson;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			init(business, id, wi);

			WoControl control = business.getControl(effectivePerson, this.task, WoControl.class);

			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, this.task);
			}
		}

		this.reset(this.task, (!wi.getKeep()), identities, manual);

		if (BooleanUtils.isNotTrue(wi.getKeep())) {
			this.processingTask();
		}

		this.processingWork();

		List<Task> newlyTasks = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			newlyTasks.addAll(emc.listEqual(Task.class, Task.series_FIELDNAME, this.series));
		}

		Record rec = this.processingRecord(this.workLog, this.task, this.taskCompletedId, newlyTasks);

		if (StringUtils.isNotEmpty(this.taskCompletedId)) {
			this.updateNextTaskIdentity(this.taskCompletedId, rec.getProperties().getNextManualTaskIdentityList(),
					task.getJob());
		}

		if (!taskCompleteds.isEmpty()) {
			this.updatePrevTaskIdentity(
					ListTools.extractField(newlyTasks, JpaObject.id_FIELDNAME, String.class, true, true),
					taskCompleteds, this.task);
		}
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	private void init(Business business, String id, Wi wi) throws Exception {
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

		if (!work.getManualTaskIdentityMatrix().contains(task.getIdentity())) {
			throw new ExceptionIdentityNotInMatrix(task.getIdentity());
		}
		this.manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);

		this.identities = ListTools.trim(business.organization().identity().list(wi.getIdentityList()), true, true);
		// 为办理的前的所有已办,用于在record中记录当前待办转为已办时的上一处理人
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
			// 获得已办id
			this.taskCompletedId = resp.getId();
		}
	}

	private void reset(Task task, boolean remove, List<String> identities, Manual manual) throws Exception {
		V2AddManualTaskIdentityMatrixWi req = new V2AddManualTaskIdentityMatrixWi();
		req.setIdentity(task.getIdentity());
		req.setRemove(remove);
		V2AddManualTaskIdentityMatrixWi.Option option = new V2AddManualTaskIdentityMatrixWi.Option();
		option.setIdentityList(identities);
		if (Objects.equals(ManualMode.single, manual.getManualMode())
				|| Objects.equals(ManualMode.grab, manual.getManualMode())) {
			option.setPosition(ManualTaskIdentityMatrix.ADD_POSITION_EXTEND);
		} else {
			option.setPosition(ManualTaskIdentityMatrix.ADD_POSITION_AFTER);
		}
		List<V2AddManualTaskIdentityMatrixWi.Option> optionList = new ArrayList<>();
		optionList.add(option);
		req.setOptionList(optionList);
		WrapBoolean resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class, Applications.joinQueryUri("work", "v2",
						task.getWork(), "add", "manual", "task", "identity", "matrix"), req, task.getJob())
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
		RecordBuilder.processing(r);
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