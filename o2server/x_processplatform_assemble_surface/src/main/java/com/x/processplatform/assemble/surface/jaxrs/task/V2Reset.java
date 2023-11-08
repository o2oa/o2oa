package com.x.processplatform.assemble.surface.jaxrs.task;

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
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V2ResetWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V2ResetWo;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo;

/**
 * @since 8.2 重置处理人
 */

public class V2Reset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Param param = this.init(effectivePerson, id, jsonElement);
		updateTask(param.task.getId(), param.routeName, param.opinion, param.task.getJob());
		reset(param.task.getId(), param.distinguishedNameList, param.task.getJob());
		String taskCompletedId = this.processingTask(param.task.getId(), param.task.getJob());
		this.processingWork(param.work.getId(), param.series, param.work.getJob());
		Record rec = this.recordTaskProcessing(Record.TYPE_RESET, param.workLog.getJob(), param.workLog.getId(),
				taskCompletedId, param.series);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.opinion = wi.getOpinion();
		param.routeName = wi.getRouteName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Task task = business.entityManagerContainer().find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (StringUtils.isBlank(task.getLabel())) {
				throw new ExceptionBeforeV82TaskUnsupportedReset(id);
			}
			param.task = task;
			Work work = business.entityManagerContainer().find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowReset().build();
			if (BooleanUtils.isFalse(control.getAllowManage()) && BooleanUtils.isFalse(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			param.work = work;
			WorkLog workLog = business.entityManagerContainer().firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
					task.getJob(), WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.workLog = workLog;
			Manual manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);
			if (null == manual) {
				throw new ExceptionEntityNotExist(work.getActivity(), Manual.class);
			}
			// 将getDistinguishedNameList和getIdentityList进行合并,兼容前端接口
			param.distinguishedNameList = business.organization().distinguishedName()
					.list(ListUtils.sum(wi.getDistinguishedNameList(), wi.getIdentityList())).stream().distinct()
					.collect(Collectors.toList());
		}
		return param;
	}

	private void updateTask(String taskId, String routeName, String opinion, String job) throws Exception {
		if (StringUtils.isNotEmpty(opinion) || StringUtils.isNotEmpty(routeName)) {
			V2EditWi req = new V2EditWi();
			req.setOpinion(opinion);
			req.setRouteName(routeName);
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("task", "v2", taskId), req, job).getData(WoId.class);
		}
	}

	private void reset(String taskId, List<String> distinguishedNameList, String job) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi req = new com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi();
		req.setDistinguishedNameList(distinguishedNameList);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", "v2", taskId, "reset"), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWo.class);
	}

	private String processingTask(String taskId, String job) throws Exception {
		ActionProcessingWi req = new ActionProcessingWi();
		req.setProcessingType(TaskCompleted.PROCESSINGTYPE_RESET);
		WoId resp = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", taskId, "processing"), req, job).getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionProcessingTask(taskId);
		} else {
			return resp.getId();
		}
	}

	private void processingWork(String workId, String series, String job) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_RESET);
		req.setSeries(series);
		ActionProcessingWo resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", workId, "processing"), req, job)
				.getData(ActionProcessingWo.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new ExceptionWorkProcessing(workId);
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

	}

	public static class Wi extends V2ResetWi {

		private static final long serialVersionUID = 5747688678118966913L;

	}

	public static class Wo extends V2ResetWo {

		private static final long serialVersionUID = -4700549313374917582L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}