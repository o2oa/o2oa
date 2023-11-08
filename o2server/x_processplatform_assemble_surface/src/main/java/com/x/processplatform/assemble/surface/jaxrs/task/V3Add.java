package com.x.processplatform.assemble.surface.jaxrs.task;

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
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V3AddWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.V3AddWo;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionProcessingWo;

/**
 * @since 8.2 tickets 加签
 */
public class V3Add extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Add.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		Param param = this.init(effectivePerson, id, jsonElement);

		if (StringUtils.isNotEmpty(param.opinion) || StringUtils.isNotEmpty(param.routeName)) {
			updateTask(param.task, param.opinion, param.routeName);
		}

		this.add(param.task, param.distinguishedNameList, param.before, param.mode);
		String taskCompletedId = this.processingTask(param.task);
		this.processingWork(param.task, param.series);
		Record rec = this.recordTaskProcessing(Record.TYPE_TASKADD, param.workLog.getJob(), param.workLog.getId(),
				taskCompletedId, param.series);
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
			if (StringUtils.isBlank(task.getLabel())) {
				throw new ExceptionBeforeV82TaskUnsupportedAdd(id);
			}
			param.task = task;
			Work work = emc.find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowAddTask().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage()) && BooleanUtils.isNotTrue(control.getAllowAddTask())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			param.opinion = wi.getOpinion();
			param.routeName = wi.getRouteName();
			param.before = BooleanUtils.isNotFalse(wi.getBefore());
			param.mode = wi.getMode();
			param.distinguishedNameList = business.organization().distinguishedName()
					.list(wi.getDistinguishedNameList());
			checkDistinguishedNameList(wi.getDistinguishedNameList(), param.distinguishedNameList);
			WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.workLog = workLog;
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
		ActionProcessingWi req = new ActionProcessingWi();
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
		ActionProcessingWo resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", task.getWork(), "processing"), req, task.getJob())
				.getData(ActionProcessingWo.class);
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

	public static class Wo extends V3AddWo {

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
		private WorkLog workLog;
		private String opinion;
		private String routeName;

	}
}