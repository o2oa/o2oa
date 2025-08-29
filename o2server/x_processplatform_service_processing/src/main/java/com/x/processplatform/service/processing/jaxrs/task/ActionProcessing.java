package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionProcessingWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.WorkContext;
import com.x.processplatform.service.processing.processor.AeiObjects;

class ActionProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
					() -> jsonElement);
		}

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String job;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			job = task.getJob();
		}

		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(new CallableExecute(id, wi)).get(300,
				TimeUnit.SECONDS);

	}

	private class CallableExecute implements Callable<ActionResult<Wo>> {

		private Wi wi;

		private String id;

		private CallableExecute(String id, Wi wi) {
			this.wi = wi;
			this.id = id;
		}

		private void callManualBeforeTaskScript(Business business, Task task) throws Exception {
			if (Objects.equals(task.getActivityType(), ActivityType.manual)) {
				Manual manual = business.element().get(task.getActivity(), Manual.class);
				Process process = business.element().get(task.getProcess(), Process.class);
				if ((null != manual) && (null != process)) {
					boolean processHasManualBeforeTaskScript = processHasManualBeforeTaskScript(process);
					boolean hasManualBeforeTaskScript = hasManualBeforeTaskScript(manual);
					if (processHasManualBeforeTaskScript || hasManualBeforeTaskScript) {
						Work work = business.entityManagerContainer().find(task.getWork(), Work.class);
						if (null != work) {
							evalCallManualBeforeTaskScript(business, task, manual, process,
									processHasManualBeforeTaskScript, hasManualBeforeTaskScript, work);
						}
					}
				}
			}
		}

		private void evalCallManualBeforeTaskScript(Business business, Task task, Manual manual, Process process,
				boolean processHasManualBeforeTaskScript, boolean hasManualBeforeTaskScript, Work work)
				throws Exception {
			AeiObjects aeiObjects = new AeiObjects(business, work, manual, new ProcessingAttributes());
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings();
			((WorkContext) bindings.get(GraalvmScriptingFactory.BINDING_NAME_WORKCONTEXT)).bindTask(task);
			WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
			if (processHasManualBeforeTaskScript) {
				GraalvmScriptingFactory.eval(business.element().getCompiledScript(task.getApplication(), process,
						Business.EVENT_MANUALBEFORETASK), bindings);
			}
			if (hasManualBeforeTaskScript) {
				GraalvmScriptingFactory.eval(business.element().getCompiledScript(task.getApplication(), manual,
						Business.EVENT_MANUALBEFORETASK), bindings);
			}
			workDataHelper.update(aeiObjects.getData());
			business.entityManagerContainer().commit();
		}

		private boolean hasManualBeforeTaskScript(Manual manual) {
			return ((null != manual) && (StringUtils.isNotEmpty(manual.getManualBeforeTaskScript())
					|| StringUtils.isNotEmpty(manual.getManualBeforeTaskScriptText())));
		}

		private boolean processHasManualBeforeTaskScript(Process process) {
			return ((null != process) && (StringUtils.isNotEmpty(process.getManualBeforeTaskScript())
					|| StringUtils.isNotEmpty(process.getManualBeforeTaskScriptText())));
		}

		private void callManualAfterTaskScript(Business business, TaskCompleted taskCompleted) throws Exception {
			if (Objects.equals(taskCompleted.getActivityType(), ActivityType.manual)) {
				Manual manual = business.element().get(taskCompleted.getActivity(), Manual.class);
				Process process = business.element().get(taskCompleted.getProcess(), Process.class);
				if ((null != manual) && (null != process)) {
					boolean processHasManualAfterTaskScript = processHasManualAfterTaskScript(process);
					boolean hasManualAfterTaskScript = hasManualAfterTaskScript(manual);
					if (processHasManualAfterTaskScript || hasManualAfterTaskScript) {
						Work work = business.entityManagerContainer().find(taskCompleted.getWork(), Work.class);
						if (null != work) {
							evalCallManualAfterTaskScript(business, taskCompleted, manual, process,
									processHasManualAfterTaskScript, hasManualAfterTaskScript, work);
						}
					}
				}
			}
		}

		private void evalCallManualAfterTaskScript(Business business, TaskCompleted taskCompleted, Manual manual,
				Process process, boolean processHasManualAfterTaskScript, boolean hasManualAfterTaskScript, Work work)
				throws Exception {
			AeiObjects aeiObjects = new AeiObjects(business, work, manual, new ProcessingAttributes());
			GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings();
			((WorkContext) bindings.get(GraalvmScriptingFactory.BINDING_NAME_WORKCONTEXT))
					.bindTaskCompleted(taskCompleted);
			if (processHasManualAfterTaskScript) {
				GraalvmScriptingFactory.eval(business.element().getCompiledScript(taskCompleted.getApplication(),
						process, Business.EVENT_MANUALAFTERTASK), bindings);
			}
			if (hasManualAfterTaskScript) {
				GraalvmScriptingFactory.eval(business.element().getCompiledScript(taskCompleted.getApplication(),
						manual, Business.EVENT_MANUALAFTERTASK), bindings);
			}
		}

		private boolean hasManualAfterTaskScript(Manual manual) {
			return ((null != manual) && (StringUtils.isNotEmpty(manual.getManualAfterTaskScript())
					|| StringUtils.isNotEmpty(manual.getManualAfterTaskScriptText())));
		}

		private boolean processHasManualAfterTaskScript(Process process) {
			return ((null != process) && (StringUtils.isNotEmpty(process.getManualAfterTaskScript())
					|| StringUtils.isNotEmpty(process.getManualAfterTaskScriptText())));
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				// 生成默认的Wi,用于生成默认的processType
				if (null == wi.getProcessingType()) {
					wi.setProcessingType(TaskCompleted.PROCESSINGTYPE_TASK);
				}
				Task task = emc.find(id, Task.class);
				if (null == task) {
					throw new ExceptionEntityNotExist(id, Task.class);
				}
				if (StringUtils.isNotEmpty(wi.getRouteName())) {
					task.setRouteName(wi.getRouteName());
				}
				if (StringUtils.isNotEmpty(wi.getOpinion())) {
					task.setOpinion(wi.getOpinion());
				}
				// 执行办前脚本
				callManualBeforeTaskScript(business, task);
				// 将待办转为已办
				emc.beginTransaction(TaskCompleted.class);
				emc.beginTransaction(Task.class);
				// 将所有前面的已办lastest标记false
				emc.listEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, task.getJob(),
						TaskCompleted.person_FIELDNAME, task.getPerson()).forEach(o -> o.setLatest(false));
				TaskCompleted taskCompleted = concreteTaskCompleted(business, task);
				emc.persist(taskCompleted, CheckPersistType.all);
				emc.remove(task, CheckRemoveType.all);
				// 去掉处理同一处理人不同身份待办合并处理一次processingTaskOnceUnderSamePerson(business, task);
				emc.commit();
				// 待办执行后脚本,不能修改数据.
				callManualAfterTaskScript(business, taskCompleted);
				MessageFactory.task_to_taskCompleted(taskCompleted);
				wo.setId(taskCompleted.getId());
			}
			result.setData(wo);
			return result;
		}

//		// 处理同一处理人不同身份待办合并处理一次
//		private void processingTaskOnceUnderSamePerson(Business business, Task task) throws Exception {
//			List<Task> tasks = business.entityManagerContainer().listEqualAndEqualAndNotEqual(Task.class,
//					Task.activityToken_FIELDNAME, task.getActivityToken(), Task.person_FIELDNAME, task.getPerson(),
//					JpaObject.id_FIELDNAME, task.getId());
//			if (!tasks.isEmpty()) {
//				Manual manual = (Manual) business.element().get(task.getActivity(), ActivityType.manual);
//				if ((null != manual) && BooleanUtils.isTrue(manual.getProcessingTaskOnceUnderSamePerson())) {
//					for (Task t : tasks) {
//						TaskCompleted tc = concreteTaskCompleted(t);
//						tc.setRouteName(task.getRouteName());
//						tc.setOpinion(task.getOpinion());
//						business.entityManagerContainer().persist(tc, CheckPersistType.all);
//						business.entityManagerContainer().remove(t, CheckRemoveType.all);
//					}
//				}
//			}
//		}

		private TaskCompleted concreteTaskCompleted(Business business, Task task) throws Exception {
			Date now = new Date();
			Long duration = Config.workTime().betweenMinutes(task.getStartTime(), now);
			TaskCompleted taskCompleted = new TaskCompleted(task, wi.getProcessingType(), now, duration);
			taskCompleted.onPersist();
			Work work = business.entityManagerContainer().find(task.getWork(), Work.class);
			if(work != null){
				taskCompleted.setForm(work.getForm());
			}
			return taskCompleted;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 1L;
	}

	public static class Wi extends ActionProcessingWi {

		private static final long serialVersionUID = -6828623155146710691L;

	}

}
