package com.x.processplatform.service.processing.jaxrs.taskcompleted;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpdateNextTaskIdentity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateNextTaskIdentity.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		final Bag bag = new Bag();
		bag.wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<TaskCompleted> os = emc.fetchIn(TaskCompleted.class, ListTools.toList(TaskCompleted.job_FIELDNAME),
					JpaObject.id_FIELDNAME, bag.wi.getTaskCompletedList());
			if (os.isEmpty()) {
				Wo wo = new Wo();
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			} else {
				bag.job = os.get(0).getJob();
			}
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				Wo wo = new Wo();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					List<TaskCompleted> os = emc.listIn(TaskCompleted.class, JpaObject.id_FIELDNAME,
							bag.wi.getTaskCompletedList());
					emc.beginTransaction(TaskCompleted.class);
					for (TaskCompleted o : os) {
						o.getProperties().setNextTaskIdentityList(bag.wi.getNextTaskIdentityList());
						emc.check(o, CheckPersistType.all);
						wo.getValueList().add(o.getId());
					}
					emc.commit();
				}
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(bag.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private static class Bag {
		private Wi wi;
		private String job;
	}

	public static class Wo extends WrapStringList {

		private static final long serialVersionUID = -9080785224119859692L;
	}

	public static class Wi extends WrapUpdateNextTaskIdentity {

		private static final long serialVersionUID = -350603662290508983L;

	}

}