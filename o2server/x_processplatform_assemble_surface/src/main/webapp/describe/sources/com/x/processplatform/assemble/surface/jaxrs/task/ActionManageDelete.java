package com.x.processplatform.assemble.surface.jaxrs.task;

import java.net.URLEncoder;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.element.Application;

class ActionManageDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id,Task.class);
			}
			Application application = business.application().pick(task.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(task.getApplication(), Application.class);
			}
			// if (!StringUtils.equals(task.getApplication(),
			// application.getId())) {
			// throw new ApplicationNotMatchException(application.getId(),
			// task.getApplication());
			// }
			/** 需要对这个应用的管理权限 */
			if (!business.application().allowControl(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson, application);
			}
			ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
					"task/" + URLEncoder.encode(task.getId(), DefaultCharset.name));
			Wo wo = new Wo();
			wo.setId(task.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}
}