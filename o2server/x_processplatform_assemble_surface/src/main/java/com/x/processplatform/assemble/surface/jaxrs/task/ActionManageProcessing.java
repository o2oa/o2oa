package com.x.processplatform.assemble.surface.jaxrs.task;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionManageProcessingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageProcessing extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Task task = null;
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			Application application = business.application().pick(task.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(task.getApplication(), Application.class);
			}
			Process process = business.process().pick(task.getProcess());
			// 需要对这个应用的管理权限
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Task.class);
			// 如果有输入新的路由决策覆盖原有决策
			if (StringUtils.isNotEmpty(wi.getRouteName())) {
				task.setRouteName(wi.getRouteName());
			}
			// 如果有新的流程意见那么覆盖原有流程意见
			if (StringUtils.isNotEmpty(wi.getOpinion())) {
				task.setOpinion(wi.getOpinion());
			}
			emc.commit();
		}
		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("task", task.getId(), "processing"), null, task.getJob());
		Wo wo = new Wo();
		wo.setId(task.getId());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageProcessing.Wi")
	public static class Wi extends ActionManageProcessingWi {

		private static final long serialVersionUID = -4726539076530209219L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageProcessing.Wo")
	public static class Wo extends WoId {
	}

}
