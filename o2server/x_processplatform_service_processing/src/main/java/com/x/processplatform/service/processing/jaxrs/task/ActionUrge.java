package com.x.processplatform.service.processing.jaxrs.task;

import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.MessageFactory;

class ActionUrge extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUrge.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
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
							emc.beginTransaction(Task.class);
							task.setUrged(true);
							emc.commit();
							MessageFactory.task_urge(task);
							logger.print("催办待办, person: {}, id: {}, title: {}, sequence:{}.", task.getPerson(),
									task.getId(), task.getTitle(), task.getSequence());
						}
					} catch (Exception e) {
						throw new ExceptionExpired(e, taskId, taskTitle, taskSequence);
					}
				} catch (Exception e) {
					logger.error(e);
				}
				ActionResult<Wo> result = new ActionResult<>();
				return result;
			}
		};

		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

}