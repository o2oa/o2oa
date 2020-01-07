package com.x.processplatform.service.processing.jaxrs.taskcompleted;

import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.TaskCompleted;

class ActionUpdateNextTaskIdentityListText extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionUpdateNextTaskIdentityListText.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskCompleted taskCompleted = emc.fetch(id, TaskCompleted.class,
					ListTools.toList(TaskCompleted.job_FIELDNAME));
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(id, TaskCompleted.class);
			}
			executorSeed = taskCompleted.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
					if (null == taskCompleted) {
						throw new ExceptionEntityNotExist(id, TaskCompleted.class);
					}
					emc.beginTransaction(TaskCompleted.class);
					taskCompleted.setNextTaskIdentityListText(wi.getNextTaskIdentityListText());
					emc.commit();
				}
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

	}

	public static class Wo extends WrapBoolean {
	}

	public static class Wi extends TaskCompleted {
		private static final long serialVersionUID = -856825389708370355L;
		static WrapCopier<Wi, TaskCompleted> copier = WrapCopierFactory.wi(Wi.class, TaskCompleted.class,
				ListTools.toList(TaskCompleted.nextTaskIdentityListText_FIELDNAME), null);

	}

}