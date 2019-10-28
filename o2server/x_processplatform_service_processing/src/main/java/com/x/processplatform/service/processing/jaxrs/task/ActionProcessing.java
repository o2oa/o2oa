package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.WorkDataHelper;

class ActionProcessing extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (null == wi) {
				wi = new Wi();
			}
			/** 生成默认的Wi,用于生成默认的processType */
			if (null == wi.getProcessingType()) {
				wi.setProcessingType(ProcessingType.processing);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			Manual manual = null;
			/* 执行办前脚本 */
			if (Objects.equals(task.getActivityType(), ActivityType.manual)) {
				manual = business.element().get(task.getActivity(), Manual.class);
				if (null != manual) {
					if (StringUtils.isNotEmpty(manual.getManualBeforeTaskScript())
							|| StringUtils.isNotEmpty(manual.getManualBeforeTaskScriptText())) {
						Work work = emc.find(task.getWork(), Work.class);
						Data data = new Data();
						if (null != work) {
							WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
							data = workDataHelper.get();
							ScriptHelper sh = ScriptHelperFactory.createWithTask(business, work, data, manual, task);
							sh.eval(work.getApplication(), manual.getManualBeforeTaskScript(),
									manual.getManualBeforeTaskScriptText());
							if (workDataHelper.update(data)) {
								emc.commit();
							}
						}
					}
				}
			}
			/* 将待办转为已办 */
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Task.class);
			/* 将所有前面的已办lastest标记false */
			emc.listEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, task.getJob(),
					TaskCompleted.person_FIELDNAME, task.getPerson()).forEach(o -> {
						o.setLatest(false);
					});
			Date now = new Date();
			Long duration = Config.workTime().betweenMinutes(task.getStartTime(), now);
			TaskCompleted taskCompleted = new TaskCompleted(task, wi.getProcessingType(), now, duration);
			taskCompleted.onPersist();
			emc.persist(taskCompleted, CheckPersistType.all);
			emc.remove(task, CheckRemoveType.all);
			emc.commit();
			/* 待办执行后脚本 */
			if (null != manual) {
				if (StringUtils.isNotEmpty(manual.getManualAfterTaskScript())
						|| StringUtils.isNotEmpty(manual.getManualAfterTaskScriptText())) {
					Work work = emc.find(task.getWork(), Work.class);
					Data data = new Data();
					if (null != work) {
						WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
						data = workDataHelper.get();
						ScriptHelper sh = ScriptHelperFactory.createWithTaskCompleted(business, work, data, manual,
								taskCompleted);
						sh.eval(work.getApplication(), manual.getManualAfterTaskScript(),
								manual.getManualAfterTaskScriptText());
						if (workDataHelper.update(data)) {
							emc.commit();
						}
					}
				}
			}
			MessageFactory.task_to_taskCompleted(taskCompleted);
//			if (BooleanUtils.isNotFalse(wi.getFinallyProcessingWork())) {
//				ProcessingAttributes processingAttributes = new ProcessingAttributes();
//				processingAttributes.setDebugger(effectivePerson.getDebugger());
//				Processing processing = new Processing(processingAttributes);
//				processing.processing(task.getWork());
//			}
			Wo wo = new Wo();
			wo.setId(task.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("流转类型.")
		private ProcessingType processingType;

//		@FieldDescribe("最后是否触发work的流转,默认流转.")
//		private Boolean finallyProcessingWork;

		@FieldDescribe("路由数据.")
		private JsonElement routeData;

		public ProcessingType getProcessingType() {
			return processingType;
		}

		public void setProcessingType(ProcessingType processingType) {
			this.processingType = processingType;
		}

		public JsonElement getRouteData() {
			return routeData;
		}

		public void setRouteData(JsonElement routeData) {
			this.routeData = routeData;
		}

	}

}
