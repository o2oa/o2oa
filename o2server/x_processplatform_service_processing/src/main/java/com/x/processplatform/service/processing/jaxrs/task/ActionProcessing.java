package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.ApplicationDictHelper;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WorkContext;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AeiObjects;

class ActionProcessing extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			executorSeed = task.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/** 生成默认的Wi,用于生成默认的processType */
					if (null == wi.getProcessingType()) {
						wi.setProcessingType(ProcessingType.processing);
					}
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

								AeiObjects aeiObjects = new AeiObjects(business, work, manual,
										new ProcessingConfigurator(), new ProcessingAttributes());

								if (null != work) {

									ScriptContext scriptContext = aeiObjects.scriptContext();

									CompiledScript cs = null;
									cs = business.element().getCompiledScript(task.getApplication(), manual,
											Business.EVENT_MANUALBEFORETASK);
									cs.eval(scriptContext);

									emc.commit();
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
					if (StringUtils.isEmpty(taskCompleted.getOpinion())) {
						Process process = business.element().get(task.getProcess(), Process.class);
						if ((null != process) && process.getRouteNameAsOpinion()) {
							taskCompleted.setOpinion(StringUtils.trimToEmpty(ListTools.parallel(task.getRouteNameList(),
									task.getRouteName(), task.getRouteOpinionList())));
						}
					}
					taskCompleted.onPersist();
					emc.persist(taskCompleted, CheckPersistType.all);
					emc.remove(task, CheckRemoveType.all);
					emc.commit();
					/* 待办执行后脚本 */
					if (null != manual) {
						if (StringUtils.isNotEmpty(manual.getManualAfterTaskScript())
								|| StringUtils.isNotEmpty(manual.getManualAfterTaskScriptText())) {
							Work work = emc.find(task.getWork(), Work.class);

							if (null != work) {

								AeiObjects aeiObjects = new AeiObjects(business, work, manual,
										new ProcessingConfigurator(), new ProcessingAttributes());

								ScriptContext scriptContext = aeiObjects.scriptContext();

								CompiledScript cs = null;
								cs = business.element().getCompiledScript(task.getApplication(), manual,
										Business.EVENT_MANUALAFTERTASK);

								cs.eval(scriptContext);
								
								emc.commit();
							
							}
						}
					}
					MessageFactory.task_to_taskCompleted(taskCompleted);
					wo.setId(taskCompleted.getId());
				}
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

	}

	private ScriptContext scriptContext(Business business, Manual manual, Work work, Data data, Task task)
			throws Exception {
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(ScriptFactory.BINDING_NAME_WORKCONTEXT, new WorkContext(business, work, manual, task));
		bindings.put(ScriptFactory.BINDING_NAME_GSON, XGsonBuilder.instance());
		bindings.put(ScriptFactory.BINDING_NAME_DATA, data);
		bindings.put(ScriptFactory.BINDING_NAME_ORGANIZATION, business.organization());
		bindings.put(ScriptFactory.BINDING_NAME_WEBSERVICESCLIENT, new WebservicesClient());
		bindings.put(ScriptFactory.BINDING_NAME_DICTIONARY,
				new ApplicationDictHelper(business.entityManagerContainer(), work.getApplication()));
		bindings.put(ScriptFactory.BINDING_NAME_APPLICATIONS, ThisApplication.context().applications());
		ScriptFactory.initialScriptText().eval(scriptContext);
		return scriptContext;
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("流转类型.")
		private ProcessingType processingType;

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
