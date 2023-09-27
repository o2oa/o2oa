package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionPassExpired extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPassExpired.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = task.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setValue(false);
				String taskId = null;
				String taskTitle = null;
				String taskSequence = null;
				try {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Task task = emc.find(id, Task.class);
						if (null != task) {
							taskId = task.getId();
							taskTitle = task.getTitle();
							taskSequence = task.getSequence();
							Business business = new Business(emc);
							Manual manual = manual(business, task);
							if (null == manual) {
								throw new ExceptionEntityNotExist(task.getActivity(), Manual.class);
							}
							Route route = route(business, task, manual);
							if (null == route) {
								throw new ExceptionPassExpiredRoute(taskId, taskTitle, taskSequence);
							}
							emc.beginTransaction(Task.class);
							task.setRouteName(route.getName());
							emc.commit();
							wo.setValue(true);
							MessageFactory.task_expire(task);
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 857072729127236835L;

	}

	private Manual manual(Business business, Task task) throws Exception {
		Activity activity = business.element().get(task.getActivity(), ActivityType.manual);
		if (null != activity) {
			return (Manual) activity;
		} else {
			return null;
		}
	}

	private Route route(Business business, Task task, Manual manual) throws Exception {
		List<Route> os = business.entityManagerContainer().listEqualAndIn(Route.class, Route.passExpired_FIELDNAME,
				true, Route.id_FIELDNAME, manual.getRouteList());
		if (!os.isEmpty()) {
			return os.get(0);
		}
		return null;
	}

}