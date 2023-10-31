package com.x.processplatform.assemble.surface.jaxrs.task;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionWillWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionWill extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionWill.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			Control control = new JobControlBuilder(effectivePerson, business, task.getJob())
					.enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			Wo wo = get(task);
			result.setData(wo);
			return result;
		}
	}

	private Wo get(Task task) throws Exception {
		return ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", task.getId(), "will"), task.getJob()).getData(Wo.class);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionWill.Wo")
	public static class Wo extends ActionWillWo {

		private static final long serialVersionUID = 2279846765261247910L;

	}

}
