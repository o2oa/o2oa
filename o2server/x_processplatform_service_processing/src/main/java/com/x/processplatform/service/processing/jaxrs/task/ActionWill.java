package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionWillWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.manual.TaskIdentities;
import com.x.processplatform.service.processing.processor.manual.TranslateTaskIdentityTools;

class ActionWill extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		String executorSeed = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));

			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}

			executorSeed = task.getJob();
		}

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(new CallableImpl(id)).get(300,
				TimeUnit.SECONDS);
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		private CallableImpl(String id) {
			this.id = id;
		}

		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Wo wo = new Wo();
				Task task = emc.find(id, Task.class);
				Work work = emc.find(task.getWork(), Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(task.getWork(), Work.class);
				}
				Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);
				if (null == manual) {
					throw new ExceptionEntityNotExist(work.getActivity(), Manual.class);
				}
				if (BooleanUtils.isTrue(manual.getAllowRapid())) {
					wo.setAllowRapid(true);
				}
				List<Route> routes = business.element().listRouteWithManual(manual.getId());
				Route route = null;
				if (routes.size() == 1) {
					route = routes.get(0);
				} else {
					Optional<Route> optional = routes.stream().filter(o -> BooleanUtils.isTrue(o.getSole()))
							.findFirst();
					if (optional.isPresent()) {
						route = optional.get();
						wo.setHasSole(route.getSole());
					}
				}
				if (null != route) {
					wo.setDefaultRouteName(route.getName());
					wo.setNextActivityType(route.getActivityType());
					Activity nextActivity = business.element().get(route.getActivity(), route.getActivityType());
					if (null != nextActivity) {
						wo.setNextActivityName(nextActivity.getName());
						wo.setNextActivityAlias(nextActivity.getAlias());
						wo.setNextActivityDescription(nextActivity.getDescription());
						if (Objects.equals(ActivityType.manual, nextActivity.getActivityType())) {
							Manual nextManual = (Manual) nextActivity;
							AeiObjects aeiObjects = new AeiObjects(business, work, nextManual,
									new ProcessingAttributes());
							TaskIdentities taskIdentities = TranslateTaskIdentityTools.translate(aeiObjects,
									nextManual);
							wo.setNextTaskIdentityList(taskIdentities.identities());
						}
					}
				}
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wo extends ActionWillWo {

		private static final long serialVersionUID = 663543271322651720L;
	}

}