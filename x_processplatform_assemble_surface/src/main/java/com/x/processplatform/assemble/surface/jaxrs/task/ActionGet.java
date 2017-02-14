package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.core.entity.content.Task;

class ActionGet extends ActionBase {

	ActionResult<WrapOutTask> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutTask> result = new ActionResult<>();
			Task task = emc.find(id, Task.class, ExceptionWhen.not_found);
			if ((effectivePerson.isNotManager()) && (effectivePerson.isNotUser(task.getPerson()))) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access task{id:" + task.getId()
						+ "} was denied.");
			}
			WrapOutTask wrap = taskOutCopier.copy(task);
			result.setData(wrap);
			return result;
		}
	}

}
