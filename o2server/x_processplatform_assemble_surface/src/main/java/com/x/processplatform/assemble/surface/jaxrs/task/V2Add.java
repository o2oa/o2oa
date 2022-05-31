package com.x.processplatform.assemble.surface.jaxrs.task;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
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
import com.x.processplatform.assemble.surface.RecordBuilder;
import com.x.processplatform.assemble.surface.TaskBuilder;
import com.x.processplatform.assemble.surface.TaskCompletedBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2AddWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWi.Option;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWo;

public class V2Add extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Add.class);
	// 当前提交的串号
	private final String series = StringTools.uniqueToken();
	// 新创建的待办标识列表
	private List<String> newTaskIds = new ArrayList<>();
	// 当前待办转成已办得到的已办id
	private String taskCompletedId;
	// 已经存在的待办标识列表
	private List<String> existTaskIds = new ArrayList<>();
	// 输入
	private Wi wi;
	// 当前执行用户
	private EffectivePerson effectivePerson;
	// 根据输入得到的待办
	private Task task = null;
	// 当前待办的workLog
	private WorkLog workLog = null;
	// 本环节创建的record
	private Record rec = null;
	// 执行前已经存在的已办
	private List<TaskCompleted> taskCompleteds;
	// 返回的ManualTaskIdentityMatrix
	private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		}
		this.init(effectivePerson, id, jsonElement);
		this.manualTaskIdentityMatrix = this.add(this.task, wi.getOptionList(), wi.getRemove());
		if (BooleanUtils.isTrue(wi.getRemove())) {
			taskCompletedId = this.processingTask(this.task);
		}

		if (StringUtils.isNotEmpty(wi.getOpinion()) || StringUtils.isNotEmpty(wi.getRouteName())) {
			updateTask(wi.getOpinion(), wi.getRouteName());
		}

		this.processingWork(this.task);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.newTaskIds = ListUtils.subtract(emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, task.getJob(),
					Task.work_FIELDNAME, task.getWork()), existTaskIds);
		}

		this.rec = RecordBuilder.ofTaskProcessing(Record.TYPE_TASKADD, workLog, task, taskCompletedId, newTaskIds);
		RecordBuilder.processing(rec);

		if (StringUtils.isNotEmpty(taskCompletedId)) {
			TaskCompletedBuilder.updateNextTaskIdentity(this.taskCompletedId,
					rec.getProperties().getNextManualTaskIdentityList(), task.getJob());
		}
		TaskBuilder.updatePrevTaskIdentity(this.newTaskIds, this.taskCompleteds, this.task);
		return result();
	}

	private void updateTask(String opinion, String routeName) throws Exception {
		V2EditWi req = new V2EditWi();
		req.setOpinion(opinion);
		req.setRouteName(routeName);
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", task.getId()), req, task.getJob()).getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionUpdateTask(task.getId());
		}
	}

	private void init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.effectivePerson = effectivePerson;
			this.task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			this.wi = this.convertToWrapIn(jsonElement, Wi.class);
			this.initFilterOptionIdentities(business, wi, task.getWork());
			this.initCheckOptionIdentities(wi);
			this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob(),
					WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			WoControl control = business.getControl(effectivePerson, task, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			this.existTaskIds = emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, task.getJob(), Task.work_FIELDNAME,
					task.getWork());
			this.taskCompleteds = business.entityManagerContainer().listEqual(TaskCompleted.class,
					TaskCompleted.activityToken_FIELDNAME, task.getActivityToken());
		}
	}

	private void initFilterOptionIdentities(Business business, Wi wi, String workId) throws Exception {
		Work work = business.entityManagerContainer().find(workId, Work.class);
		if (null == work) {
			throw new ExceptionEntityNotExist(workId, Task.class);
		}
		List<String> identities = work.getManualTaskIdentityMatrix().flat();
		for (Option option : wi.getOptionList()) {
			Iterator<String> iterator = option.getIdentityList().iterator();
			while (iterator.hasNext()) {
				if (identities.contains(iterator.next())) {
					iterator.remove();
				}
			}
		}
	}

	private void initCheckOptionIdentities(Wi wi) throws Exception {
		Optional<List<String>> optional = wi.getOptionList().stream().map(Option::getIdentityList)
				.filter(o -> !o.isEmpty()).findAny();
		if (optional.isEmpty()) {
			throw new ExceptionEmptyOption();
		}
	}

	private ManualTaskIdentityMatrix add(Task task, List<V2AddManualTaskIdentityMatrixWi.Option> options,
			Boolean remove) throws Exception {
		V2AddManualTaskIdentityMatrixWi req = new V2AddManualTaskIdentityMatrixWi();
		req.setIdentity(task.getIdentity());
		req.setOptionList(options);
		req.setRemove(remove);
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", task.getWork(), "add", "manual", "task", "identity",
								"matrix"),
						req, task.getJob())
				.getData(V2AddManualTaskIdentityMatrixWo.class).getManualTaskIdentityMatrix();
	}

	private String processingTask(Task task) throws Exception {
		ProcessingWi req = new ProcessingWi();
		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_ADD);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionProcessingTask(task.getId());
		} else {
			return resp.getId();
		}
	}

	private void processingWork(Task task) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_TASKADD);
		req.setSeries(this.series);
		WoId resp = ThisApplication.context().applications()
				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionWorkProcessing(task.getWork());
		}
	}

//	private void updateTaskCompleted() throws Exception {
//		// 记录下一处理人信息
//		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
//		req.getTaskCompletedList().add(this.taskCompletedId);
//		req.setNextTaskIdentityList(rec.getProperties().getNextManualTaskIdentityList());
//		ThisApplication.context().applications()
//				.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, task.getJob())
//				.getData(WrapBoolean.class);
//	}
//
//	private void updateTask() throws Exception {
//		// 记录上一处理人信息
//		if (ListTools.isNotEmpty(this.newTaskIds)) {
//			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
//			req.setTaskList(this.newTaskIds);
//			req.setPrevTaskIdentity(task.getIdentity());
//			req.getPrevTaskIdentityList().add(task.getIdentity());
//			ThisApplication.context().applications()
//					.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
//					.getData(WrapBoolean.class);
//		}
//	}

	private ActionResult<Wo> result() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(this.rec);
		wo.setManualTaskIdentityMatrix(this.manualTaskIdentityMatrix);
		result.setData(wo);
		return result;
	}

	public static class Wi extends V2AddWi {

		private static final long serialVersionUID = -6251874269093504136L;

		@FieldDescribe("路由名称")
		private String routeName;

		@FieldDescribe("意见")
		private String opinion;

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

	}

	public static class WoControl extends WorkControl {

		private static final long serialVersionUID = -8675239528577375846L;

	}

	public static class Wo extends Record {

		private static final long serialVersionUID = 1416972392523085640L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

		public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
			return manualTaskIdentityMatrix;
		}

		public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
			this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
		}

	}

}