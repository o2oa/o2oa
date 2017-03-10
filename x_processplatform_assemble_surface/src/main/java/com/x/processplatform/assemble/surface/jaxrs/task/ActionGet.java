package com.x.processplatform.assemble.surface.jaxrs.task;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.core.entity.content.Task;

class ActionGet extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<WrapOutTask> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutTask> result = new ActionResult<>();
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new TaskNotExistedException(id);
			}
			if ((effectivePerson.isNotManager()) && (effectivePerson.isNotUser(task.getPerson()))) {
				throw new TaskAccessDeniedException(effectivePerson.getName(), id);
			}
			WrapOutTask wrap = taskOutCopier.copy(task);
			result.setData(wrap);
			return result;
		}
	}

}
