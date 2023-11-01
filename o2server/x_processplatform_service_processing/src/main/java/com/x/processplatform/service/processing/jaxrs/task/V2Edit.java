package com.x.processplatform.service.processing.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2EditWo;

public class V2Edit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Edit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			emc.beginTransaction(Task.class);
			task.setRouteName(wi.getRouteName());
			task.setOpinion(wi.getOpinion());
			emc.check(task, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(task.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends V2EditWo {

		private static final long serialVersionUID = 1363997520866820672L;
	}

	public static class Wi extends V2EditWi {

		private static final long serialVersionUID = -4726539076530209219L;

	}
}