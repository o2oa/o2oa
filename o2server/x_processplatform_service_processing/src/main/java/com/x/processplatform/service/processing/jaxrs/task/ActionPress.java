package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionPressWo;
import com.x.processplatform.service.processing.MessageFactory;

class ActionPress extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPress.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			MessageFactory.task_press(task, "");
			wo.setValue(task.getPerson());
		}

		result.setData(wo);
		return result;

	}

	public static class Wo extends ActionPressWo {

		private static final long serialVersionUID = 6127898394613135325L;
	}

}