package com.x.processplatform.assemble.surface.jaxrs.task;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
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
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V2TriggerProcessingWo;

class V2TriggerProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2TriggerProcessing.class);

	private static final String STRING_PROCESSING = "processing";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(effectivePerson, id);

		String taskCompletedId = this.processingTask(param, TaskCompleted.PROCESSINGTYPE_TASK);
		this.processingWork(param, ProcessingAttributes.TYPE_TASK);
		boolean flag = true;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 流程流转到取消环节，此时工作已被删除.flag =true 代表存在,false 已经被删除
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, param.task.getJob()) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, param.task.getJob()) == 0)) {
				flag = false;
			}
		}
		Record rec = null;
		if (flag) {
			rec = this.recordTaskProcessing(Record.TYPE_TASKTRIGGERPROCESSING, param.workLog.getJob(), param.workLog.getId(),
					taskCompletedId, param.series);
		} else {
//			// 这里的record不需要写入到数据库,work和workCompleted都消失了,可能走了cancel环节,这里的rec仅作为返回值生成wo
			rec = new Record(param.workLog);
			rec.setType(Record.TYPE_TASKTRIGGERPROCESSING);
			rec.setOpinion(param.task.getOpinion());
			rec.setRouteName(param.task.getRouteName());
			rec.setCompleted(true);
		}
		manualAfterProcessing(param, rec);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.task = task;
			Work work = emc.find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
			}
			param.work = work;
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowProcessing().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())
					&& BooleanUtils.isNotTrue(control.getAllowProcessing())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.workLog = workLog;
		}
		return param;
	}

	private class Param {

		private Work work;
		private Task task;
		private WorkLog workLog;
		private String series = StringTools.uniqueToken();

	}

	/**
	 * 调用人工环节工作流转后执行脚本
	 * 
	 * @throws Exception
	 */
	private void manualAfterProcessing(Param param, Record rec) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWi();
		req.setTask(param.task);
		req.setRecord(rec);
		ThisApplication.context().applications().postQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", "manual", "after", STRING_PROCESSING), req, param.work.getJob())
				.getData(
						com.x.processplatform.core.express.service.processing.jaxrs.work.ActionManualAfterProcessingWo.class);
	}

	private String processingTask(Param param, String processType) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi req = new com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi();
		req.setProcessingType(processType);
		com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWo resp = ThisApplication
				.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", param.task.getId(), STRING_PROCESSING), req,
						param.task.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWo.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionProcessingTask(param.task.getId());
		} else {
			// 获得已办id
			return resp.getId();
		}
	}

	private void processingWork(Param param, String workProcessingType) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(workProcessingType);
		req.setSeries(param.series);
		req.setPerson(param.task.getPerson());
		req.setIdentity(param.task.getIdentity());
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", param.task.getWork(), STRING_PROCESSING), req, param.task.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionWorkProcessing(param.task.getId());
		}
	}

	public static class Wo extends V2TriggerProcessingWo {

		private static final long serialVersionUID = 1277642577488429033L;

	}

}