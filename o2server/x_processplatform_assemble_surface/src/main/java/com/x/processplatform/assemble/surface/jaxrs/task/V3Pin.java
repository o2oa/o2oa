package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.Date;
import java.util.Objects;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;

class V3Pin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Pin.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (effectivePerson.isNotPerson(task.getPerson()) && effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			emc.beginTransaction(Task.class);
			if (Objects.isNull(task.getOrderNumber())) {
				task.setOrderNumber((new Date()).getTime());
				wo.setValue(true);
			} else {
				task.setOrderNumber(null);
				wo.setValue(false);
			}
			emc.persist(task, CheckPersistType.all);
			emc.commit();
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 5391248470876841140L;

	}
}
