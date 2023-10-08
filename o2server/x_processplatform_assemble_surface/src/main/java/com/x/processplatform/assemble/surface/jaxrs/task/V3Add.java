package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
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
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V3AddWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;

/**
 * @since 8.2 tickets 加签
 */
public class V3Add extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Add.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);
		Param param = this.init(effectivePerson, id, jsonElement);
		if (StringUtils.isNotEmpty(param.getOpinion()) || StringUtils.isNotEmpty(param.getRouteName())) {
			updateTask(param.getTask(), param.getOpinion(), param.getRouteName());
		}
		this.add(param.getTask(), param.getDistinguishedNameList(), param.getBefore(), param.getMode());
		String taskCompletedId = this.processingTask(param.getTask());
		this.processingWork(param.getTask(), param.getSeries());
		List<String> newTaskIds = new ArrayList<>();
		// 加签计算所有处理人即可,不需要去重计算现在已有的task
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			newTaskIds = emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, param.getTask().getJob(),
					Task.work_FIELDNAME, param.getTask().getWork());
		}
		Record rec = RecordBuilder.ofTaskProcessing(Record.TYPE_TASKADD, param.getWorkLog(), param.getTask(),
				taskCompletedId, newTaskIds);
		// 加签也记录流程意见和路由决策
		rec.getProperties().setOpinion(param.getOpinion());
		rec.getProperties().setRouteName(param.getRouteName());
		RecordBuilder.processing(rec);
		if (StringUtils.isNotEmpty(taskCompletedId)) {
			TaskCompletedBuilder.updateNextTaskIdentity(taskCompletedId,
					rec.getProperties().getNextManualTaskIdentityList(), param.getTask().getJob());
		}
		TaskBuilder.updatePrevTaskIdentity(newTaskIds, param.getTaskCompleteds(), param.getTask());
		return result(rec);
	}

	private void updateTask(Task task, String opinion, String routeName) throws Exception {
		V2EditWi req = new V2EditWi();
		req.setOpinion(opinion);
		req.setRouteName(routeName);
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", "v2", task.getId()), req, task.getJob()).getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionUpdateTask(task.getId());
		}
	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.setTask(task);
			Work work = emc.find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
			}
			param.setWork(work);
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowAddTask().build();
			if (BooleanUtils.isNotTrue(control.getAllowAddTask())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			param.setOpinion(wi.getOpinion());
			param.setRouteName(wi.getRouteName());
			param.setBefore(BooleanUtils.isNotFalse(wi.getBefore()));
			param.setMode(wi.getMode());
			param.setDistinguishedNameList(
					business.organization().distinguishedName().list(wi.getDistinguishedNameList()));
			checkDistinguishedNameList(wi.getDistinguishedNameList(), param.getDistinguishedNameList());
			WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.setWorkLog(workLog);
			param.setTaskCompleteds(business.entityManagerContainer().listEqual(TaskCompleted.class,
					TaskCompleted.activityToken_FIELDNAME, task.getActivityToken()));
		}
		return param;
	}

	/**
	 * 检查传入的distinguishedNameList是否全部有效
	 * 
	 * @param list
	 * @param validList
	 * @throws ExceptionInvalidDistinguishedName
	 */
	private void checkDistinguishedNameList(List<String> list, List<String> validList)
			throws ExceptionInvalidDistinguishedName {
		List<String> subtract = ListUtils.subtract(list, validList);
		if (!subtract.isEmpty()) {
			throw new ExceptionInvalidDistinguishedName(subtract);
		}
	}

	private boolean add(Task task, List<String> distinguishedNameList, boolean before, String mode) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.task.V3AddWi req = new com.x.processplatform.core.express.service.processing.jaxrs.task.V3AddWi();
		req.setBefore(before);
		req.setDistinguishedNameList(distinguishedNameList);
		req.setMode(mode);
		return ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v3", task.getId(), "add"), req, task.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.task.V3AddWo.class).getValue();
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

	private void processingWork(Task task, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_TASKADD);
		req.setSeries(series);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
				.getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionWorkProcessing(task.getWork());
		}
	}

	private ActionResult<Wo> result(Record rec) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	public static class Wi extends V3AddWi {

		private static final long serialVersionUID = -6251874269093504136L;

	}

	public static class Wo extends Record {

		private static final long serialVersionUID = 1416972392523085640L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Param {

		private String series = StringTools.uniqueToken();
		private List<String> distinguishedNameList;
		private Boolean before;
		private String mode;
		private Task task;
		private Work work;
		private WorkLog workLog;
		private List<TaskCompleted> taskCompleteds;
		private String opinion;
		private String routeName;

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

		public List<TaskCompleted> getTaskCompleteds() {
			return taskCompleteds;
		}

		public void setTaskCompleteds(List<TaskCompleted> taskCompleteds) {
			this.taskCompleteds = taskCompleteds;
		}

		public WorkLog getWorkLog() {
			return workLog;
		}

		public void setWorkLog(WorkLog workLog) {
			this.workLog = workLog;
		}

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

		public Boolean getBefore() {
			return before;
		}

		public void setBefore(Boolean before) {
			this.before = before;
		}

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
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

	}
}