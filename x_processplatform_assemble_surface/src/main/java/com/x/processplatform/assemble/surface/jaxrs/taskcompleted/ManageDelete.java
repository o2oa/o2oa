package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.net.URLEncoder;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.element.Process;

class ManageDelete extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class, ExceptionWhen.not_found);
			Process process = business.process().pick(taskCompleted.getProcess());
			// 需要对这个应用的管理权限
			business.process().allowControl(effectivePerson, process);
			ThisApplication.applications.deleteQuery(x_processplatform_service_processing.class,
					"taskcompleted/" + URLEncoder.encode(taskCompleted.getId(), "UTF-8"), null);
			WrapOutId wrap = new WrapOutId(taskCompleted.getId());
			result.setData(wrap);
			return result;
		}
	}

}