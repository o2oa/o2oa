package com.x.processplatform.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapAppend;
import com.x.processplatform.service.processing.ApplicationDictHelper;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WorkContext;
import com.x.processplatform.service.processing.WorkDataHelper;
import com.x.processplatform.service.processing.processor.manual.TaskIdentities;
import com.x.processplatform.service.processing.processor.manual.TaskIdentity;

class ActionAppend extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAppend.class);

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
				String workId = "";
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Task task = emc.find(id, Task.class);
					if (null == task) {
						throw new ExceptionEntityNotExist(id, Task.class);
					}
					Work work = emc.find(task.getWork(), Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(task.getWork(), Work.class);
					}
					workId = work.getId();
					Manual manual = (Manual) business.element().get(task.getActivity(), ActivityType.manual);
					Route route = getRoute(business, task, manual);
					List<String> identities = new ArrayList<>();
					if (ListTools.isNotEmpty(wi.getIdentityList())) {
						identities.addAll(wi.getIdentityList());
					}
					if (route != null) {
						if (StringUtils.equals(route.getType(), Route.TYPE_APPENDTASK)
								&& StringUtils.equals(manual.getId(), route.getActivity())) {
							if (StringUtils.equals(route.getAppendTaskIdentityType(),
									Route.APPENDTASKIDENTITYTYPE_SCRIPT)) {
								Data data = new Data();
								WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(),
										work);
								data = workDataHelper.get();

								ScriptContext scriptContext = scriptContext(business, work, data, manual, task);

								CompiledScript compiledScript = business.element().getCompiledScript(
										task.getApplication(), route, Business.EVENT_ROUTEAPPENDTASKIDENTITY);

								Object objectValue = compiledScript.eval(scriptContext);

								List<String> os = ScriptFactory.asDistinguishedNameList(objectValue);

								if (ListTools.isNotEmpty(os)) {
									identities.addAll(os);
								}
							}
						}
					}
					Process process = business.element().get(task.getProcess(), Process.class);
					identities = business.organization().identity().list(ListTools.trim(identities, true, true));
					TaskIdentities taskIdentities = empower(business, process, task, identities);
					identities = taskIdentities.identities();
					if (ListTools.isNotEmpty(identities)) {
//						List<TaskCompleted> os = emc.listEqualAndInAndNotEqual(TaskCompleted.class,
//								TaskCompleted.activityToken_FIELDNAME, work.getActivityToken(),
//								TaskCompleted.identity_FIELDNAME, identities, TaskCompleted.joinInquire_FIELDNAME,
//								true);
						// 同一环节办理后再转交给已经有已办的人员,需要将已办置为joinInquire=false
						List<TaskCompleted> os = emc.listEqualAndInAndNotEqual(TaskCompleted.class,
								TaskCompleted.activityToken_FIELDNAME, work.getActivityToken(),
								TaskCompleted.identity_FIELDNAME, identities, TaskCompleted.joinInquire_FIELDNAME,
								false);
						if (ListTools.isNotEmpty(os)) {
							emc.beginTransaction(TaskCompleted.class);
							for (TaskCompleted o : os) {
								o.setJoinInquire(false);
								o.setProcessingType(TaskCompleted.PROCESSINGTYPE_BEAPPENDEDTASK);
							}
						}
						// 后面还要合并,clone一个新实例
						wo.getValueList().addAll(new ArrayList<>(identities));
						identities = ListUtils.sum(ListUtils.subtract(work.getManualTaskIdentityList(),
								ListTools.toList(task.getIdentity())), identities);
						identities = business.organization().identity().list(ListTools.trim(identities, true, true));
						emc.beginTransaction(Work.class);
						work.setManualTaskIdentityList(identities);
						for (TaskIdentity taskIdentity : taskIdentities) {
							if (BooleanUtils.isNotTrue(taskIdentity.getIgnoreEmpower())
									&& StringUtils.isNotEmpty(taskIdentity.getFromIdentity())) {
								work.getProperties().getManualEmpowerMap().put(taskIdentity.getIdentity(),
										taskIdentity.getFromIdentity());
							}
						}
						//properties中的集合对象需要重新new对象set进去，这样jpa才会更新数据
						Map<String, String> manualEmpowerMap = new LinkedHashMap<String, String>();
						manualEmpowerMap.putAll(work.getProperties().getManualEmpowerMap());
						work.getProperties().setManualEmpowerMap(manualEmpowerMap);
						// 转派后设置过期为空
//						emc.beginTransaction(Task.class);
//						task.setExpired(false);
//						task.setExpireTime(null);
						emc.commit();
					}
					result.setData(wo);
				}
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);
	}

	public static ScriptContext scriptContext(Business business, Work work, Data data, Activity activity, Task task)
			throws Exception {
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		WorkContext workContext = new WorkContext(business, work, activity, task);
		bindings.put(ScriptFactory.BINDING_NAME_WORKCONTEXT, workContext);
		bindings.put(ScriptFactory.BINDING_NAME_DATA, data);
		bindings.put(ScriptFactory.BINDING_NAME_ORGANIZATION, business.organization());
		bindings.put(ScriptFactory.BINDING_NAME_WEBSERVICESCLIENT, new WebservicesClient());
		bindings.put(ScriptFactory.BINDING_NAME_DICTIONARY,
				new ApplicationDictHelper(business.entityManagerContainer(), work.getApplication()));
		bindings.put(ScriptFactory.BINDING_NAME_APPLICATIONS, ThisApplication.context().applications());
		// 重新创建的ScriptContext是需要初始化的
		ScriptFactory.initialScriptText().eval(scriptContext);
		return scriptContext;
	}

	private TaskIdentities empower(Business business, Process process, Task task, List<String> identities)
			throws Exception {
		TaskIdentities taskIdentities = new TaskIdentities();
		taskIdentities.addIdentities(identities);
		taskIdentities.empower(business.organization().empower().listWithIdentityObject(task.getApplication(),
				process.getEdition(), task.getProcess(), task.getWork(), identities));
		return taskIdentities;
	}

	private Route getRoute(Business business, Task task, Manual manual) throws Exception {
		for (Route o : business.element().listRouteWithManual(manual.getId())) {
			if (StringUtils.equals(task.getRouteName(), o.getName())) {
				return o;
			}
		}
		return null;
	}

	public static class Wi extends WrapAppend {
	}

	public static class Wo extends WrapStringList {

	}

}
