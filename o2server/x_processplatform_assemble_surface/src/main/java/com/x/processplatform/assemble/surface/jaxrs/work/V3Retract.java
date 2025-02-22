package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RetractWo;

class V3Retract extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Retract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionResult<V3RetractStage.Wo> stageResult = new V3RetractStage().execute(effectivePerson, wi.getWork());

		List<String> workIds = stageResult.getData().getWorkList().stream().map(V3RetractStage.WoWork::getId)
				.collect(Collectors.toList());
		List<String> taskIds = stageResult.getData().getWorkList().stream()
				.flatMap(o -> o.getTaskList().stream().map(V3RetractStage.WoTask::getId)).collect(Collectors.toList());
		List<String> taskCompletedIds = stageResult.getData().getTaskCompletedList().stream()
				.map(V3RetractStage.WoTaskCompleted::getId).collect(Collectors.toList());
		if ((!workIds.containsAll(wi.getRetractWorkList())) || (!taskIds.containsAll(wi.getRetractTaskList()))
				|| (!taskCompletedIds.contains(wi.getTaskCompleted()))) {
			throw new ExceptionRetract(wi.getWork());
		}

		WorkLog workLog = null;
		TaskCompleted taskCompleted = null;
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			work = emc.find(wi.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(wi.getWork(), Work.class);
			}
			taskCompleted = emc.find(wi.getTaskCompleted(), TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(wi.getTaskCompleted(), TaskCompleted.class);
			}
			workLog = emc.firstEqual(WorkLog.class, WorkLog.FROMACTIVITYTOKEN_FIELDNAME,
					taskCompleted.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
		}

		String series = StringTools.uniqueToken();
		this.retract(wi, work.getJob());
		this.processing(wi, work.getJob(), series);

		Record rec = this.recordWorkProcessing(Record.TYPE_RETRACT, "", "", work.getJob(), workLog.getId(),
				taskCompleted.getIdentity(), series);

		result.setData(Wo.copier.copy(rec));

		return result;

	}

	private void retract(Wi wi, String job) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWi();
		req.setTaskCompleted(wi.getTaskCompleted());
		req.setWork(wi.getWork());
		com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWo resp = ThisApplication.context()
				.applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v3", "retract"), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWo.class);
		if (BooleanUtils.isNotTrue(resp.getValue())) {
			throw new ExceptionRetract(wi.getWork());
		}
	}

	private void processing(Wi wi, String job, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_RETRACT);
		req.setSeries(series);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", wi.getWork(), "processing"), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo.class);
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6034396222207463624L;

		@FieldDescribe("工作标识")
		private String work;

		@FieldDescribe("已办标识")
		private String taskCompleted;

		@FieldDescribe("撤回工作标识")
		private List<String> retractWorkList;

		@FieldDescribe("撤回待办标识")
		private List<String> retractTaskList;

		public String getTaskCompleted() {
			return taskCompleted;
		}

		public void setTaskCompleted(String taskCompleted) {
			this.taskCompleted = taskCompleted;
		}

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

		public List<String> getRetractWorkList() {
			return retractWorkList;
		}

		public void setRetractWorkList(List<String> retractWorkList) {
			this.retractWorkList = retractWorkList;
		}

		public List<String> getRetractTaskList() {
			return retractTaskList;
		}

		public void setRetractTaskList(List<String> retractTaskList) {
			this.retractTaskList = retractTaskList;
		}

	}

	public static class Wo extends V2RetractWo {

		private static final long serialVersionUID = -5007785846454720742L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
