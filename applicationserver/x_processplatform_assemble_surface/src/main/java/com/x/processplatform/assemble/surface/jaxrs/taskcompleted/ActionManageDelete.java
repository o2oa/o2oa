package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.net.URLEncoder;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageDelete extends BaseAction {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(id,TaskCompleted.class);
			}
			Process process = business.process().pick(taskCompleted.getProcess());
			Application application = business.application().pick(taskCompleted.getApplication());
			// 需要对这个应用的管理权限
			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
					"taskcompleted/" + URLEncoder.encode(taskCompleted.getId(), DefaultCharset.name));
			WrapOutId wrap = new WrapOutId(taskCompleted.getId());
			result.setData(wrap);
			return result;
		}
	}

}