package com.x.processplatform.assemble.surface.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInTask;
import com.x.processplatform.core.entity.content.Task;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInTask wrapIn = this.convertToWrapIn(jsonElement, WrapInTask.class);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new TaskNotExistedException(id);
			}
			if (effectivePerson.isNotUser(task.getPerson()) && effectivePerson.isNotManager()) {
				throw new TaskAccessDeniedException(effectivePerson.getName(), task.getId());
			}
			emc.beginTransaction(Task.class);
			taskInCopier.copy(wrapIn, task);
			emc.check(task, CheckPersistType.all);
			emc.commit();
			WrapOutId wrap = new WrapOutId(task.getId());
			result.setData(wrap);
			return result;
		}
	}
}
