package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.service.processing.MessageFactory;

class ActionPress extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionPress.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			logger.info("{}提醒待办【{}】给【{}】",effectivePerson.getDistinguishedName(),task.getTitle(),task.getPerson());
			MessageFactory.task_press(task, "系统");
			wo.setValue(task.getPerson());
		}

		result.setData(wo);
		return result;

	}

	public static class Wo extends WrapString {
	}

}