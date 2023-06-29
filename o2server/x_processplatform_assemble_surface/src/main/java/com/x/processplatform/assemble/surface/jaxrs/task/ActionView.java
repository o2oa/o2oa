package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionView extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionView.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		Task task = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.fetch(id, Task.class,
					Arrays.asList(Task.VIEWTIME_FIELDNAME, Task.person_FIELDNAME, Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (effectivePerson.isNotPerson(task.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			if (null == task.getViewTime()) {
				view(task.getId(), task.getJob());
			}
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	private void view(String id, String job) throws Exception {
		WoId resp = ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", id, "view"), job).getData(WoId.class);
		if (StringUtils.isEmpty(resp.getId())) {
			throw new IllegalStateException(id);
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionView$Wo")
	public static class Wo extends WoId {
		private static final long serialVersionUID = 8340104077391192781L;
	}

}
