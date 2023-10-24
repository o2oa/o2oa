package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

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
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RollbackWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RollbackWo;

class V2Rollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Rollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(effectivePerson, id, jsonElement);
		this.rollback(param.getWork(), param.getWorkLog(), param.getDistinguishedNameList());
		this.processing(param.getWork(), param.getSeries());
		List<String> newTaskIds = newTaskIds(param.getWork().getJob(), param.getWork().getId());
		Record rec = RecordBuilder.ofWorkProcessing(Record.TYPE_ROLLBACK, param.getWorkLog(), effectivePerson,
				param.getDestinationManual(), newTaskIds);
		RecordBuilder.processing(rec);
		Wo wo = Wo.copier.copy(rec);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;

	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		param.setSeries(StringTools.uniqueToken());
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.setWork(work);
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowRollback().build();

			if (BooleanUtils.isNotTrue(control.getAllowRollback())
					&& BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);

			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			param.setWorkLog(workLog);

			Manual manual = (Manual) business.getActivity(workLog.getFromActivity(), ActivityType.manual);

			if (null == manual) {
				throw new ExceptionEntityNotExist(workLog.getFromActivity(), Manual.class);
			}

			param.setDestinationManual(manual);

			param.setDistinguishedNameList(
					business.organization().distinguishedName().list(wi.getDistinguishedNameList()));
		}
		return param;
	}

	private class Param {

		private Work work;
		private WorkLog workLog;
		private List<String> distinguishedNameList;
		private String series;
		private Manual destinationManual;

		public Manual getDestinationManual() {
			return destinationManual;
		}

		public void setDestinationManual(Manual destinationManual) {
			this.destinationManual = destinationManual;
		}

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
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

		public String getSeries() {
			return series;
		}

		public void setSeries(String series) {
			this.series = series;
		}

	}

	private List<String> newTaskIds(String job, String work) throws Exception {
		List<String> list = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			list.addAll(emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, job, Task.work_FIELDNAME, work));
//			// 为办理的前的所有已办,用于在record中记录当前待办转为已办时的上一处理人
//			taskCompleteds = emc.listEqual(TaskCompleted.class, TaskCompleted.activityToken_FIELDNAME,
//					param.getTask().getActivityToken());
		}
		return list;
	}

	private void rollback(Work work, WorkLog workLog, List<String> distinguishedNameList) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi();
		req.setWorkLog(workLog.getId());
		req.setDistinguishedNameList(distinguishedNameList);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", work.getId(), "rollback"), req, work.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWo.class);
	}

	private void processing(Work work, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_ROLLBACK);
		req.setSeries(series);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", work.getId(), "processing"), req, work.getJob())
				.getData(WoId.class);
	}

	public static class Wi extends V2RollbackWi {

		private static final long serialVersionUID = 484540959465577790L;

	}

	public static class Wo extends V2RollbackWo {

		private static final long serialVersionUID = -8410749558739884101L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}