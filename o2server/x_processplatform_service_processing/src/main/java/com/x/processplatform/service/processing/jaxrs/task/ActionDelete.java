package com.x.processplatform.service.processing.jaxrs.task;

import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.service.processing.ExecutorServiceFactory;
import com.x.processplatform.service.processing.MessageFactory;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}

			Callable<String> callable = new Callable<String>() {
				public String call() throws Exception {
					emc.beginTransaction(Task.class);
					emc.remove(task, CheckRemoveType.all);
					emc.commit();
					MessageFactory.task_delete(task);
					Wo wo = new Wo();
					wo.setId(task.getId());
					result.setData(wo);
					return "";
				}
			};

			ExecutorServiceFactory.get(task.getJob()).submit(callable).get();

			return result;
		}
	}

	public static class Wo extends WoId {
	}

}