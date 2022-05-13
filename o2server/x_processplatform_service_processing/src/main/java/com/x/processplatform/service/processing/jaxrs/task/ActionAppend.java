package com.x.processplatform.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.script.CompiledScript;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapAppend;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.manual.TaskIdentities;
import com.x.processplatform.service.processing.processor.manual.TaskIdentity;

class ActionAppend extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAppend.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = initJob(id);

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
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
					Manual manual = (Manual) business.element().get(task.getActivity(), ActivityType.manual);
					Route route = getRoute(business, task, manual);
					List<String> identities = new ArrayList<>();
					if (ListTools.isNotEmpty(wi.getIdentityList())) {
						identities.addAll(wi.getIdentityList());
					}
					if (ifEvalScript(manual, route)) {
						evalScript(business, identities, manual, route, work, task);
					}
					Process process = business.element().get(task.getProcess(), Process.class);
					identities = business.organization().identity().list(ListTools.trim(identities, true, true));
					TaskIdentities taskIdentities = empower(business, process, task, identities);
					identities = taskIdentities.identities();
					Wo wo = new Wo();
					clean(wo, emc, business, task, work, identities, taskIdentities);
					result.setData(wo);
				}
				return result;
			}

		};

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private String initJob(String id) throws Exception {
		String executorSeed;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));

			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			executorSeed = task.getJob();
		}
		return executorSeed;
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

	private void evalScript(Business business, List<String> identities, Manual manual, Route route, Work work,
			Task task) throws Exception {
		AeiObjects aeiObjects = new AeiObjects(business, work, manual, new ProcessingConfigurator(),
				new ProcessingAttributes());
		CompiledScript compiledScript = business.element().getCompiledScript(task.getApplication(), route,
				Business.EVENT_ROUTEAPPENDTASKIDENTITY);
		JsonScriptingExecutor.evalDistinguishedNames(compiledScript, aeiObjects.scriptContext(), identities::addAll);
	}

	private boolean ifEvalScript(Manual manual, Route route) {
		return (route != null) && StringUtils.equals(route.getType(), Route.TYPE_APPENDTASK)
				&& StringUtils.equals(manual.getId(), route.getActivity())
				&& StringUtils.equals(route.getAppendTaskIdentityType(), Route.APPENDTASKIDENTITYTYPE_SCRIPT);
	}

	private void clean(Wo wo, EntityManagerContainer emc, Business business, Task task, Work work,
			List<String> identities, TaskIdentities taskIdentities) throws Exception {
		List<TaskCompleted> os = emc.listEqualAndInAndNotEqual(TaskCompleted.class,
				TaskCompleted.activityToken_FIELDNAME, work.getActivityToken(), TaskCompleted.identity_FIELDNAME,
				identities, TaskCompleted.joinInquire_FIELDNAME, false);
		if (ListTools.isNotEmpty(os)) {
			emc.beginTransaction(TaskCompleted.class);
			for (TaskCompleted o : os) {
				o.setJoinInquire(false);
				o.setProcessingType(TaskCompleted.PROCESSINGTYPE_BEAPPENDEDTASK);
			}
		}
		Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);

		if (null == manual) {
			throw new ExceptionEntityNotExist(work.getActivity(), Manual.class);
		}

		wo.getValueList().addAll(new ArrayList<>(identities));
		identities = ListUtils.sum(
				ListUtils.subtract(work.getManualTaskIdentityMatrix().flat(), ListTools.toList(task.getIdentity())),
				identities);
		identities = business.organization().identity().list(ListTools.trim(identities, true, true));
		emc.beginTransaction(Work.class);
		// work.setManualTaskIdentityList(identities);
		work.setManualTaskIdentityMatrix(manual.identitiesToManualTaskIdentityMatrix(identities));
		for (TaskIdentity taskIdentity : taskIdentities) {
			if (BooleanUtils.isNotTrue(taskIdentity.getIgnoreEmpower())
					&& StringUtils.isNotEmpty(taskIdentity.getFromIdentity())) {
				work.getProperties().getManualEmpowerMap().put(taskIdentity.getIdentity(),
						taskIdentity.getFromIdentity());
			}
		}
		// properties中的集合对象需要重新new对象set进去，这样jpa才会更新数据
		Map<String, String> manualEmpowerMap = new LinkedHashMap<>();
		manualEmpowerMap.putAll(work.getProperties().getManualEmpowerMap());
		work.getProperties().setManualEmpowerMap(manualEmpowerMap);
		emc.commit();
	}

	public static class Wi extends WrapAppend {

		private static final long serialVersionUID = -3293122515327864483L;

	}

	public static class Wo extends WrapStringList {

		private static final long serialVersionUID = 6457473592503074552L;

	}

}
