package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionManageOpinionWi;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageOpinion extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageOpinion.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			Process process = business.process().pick(task.getProcess());
			Application application = business.application().pick(task.getApplication());
			// 需要对这个应用的管理权限
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Read.class);
			task.setOpinion(Objects.toString(wi.getOpinion(), ""));
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageOpinion.Wi")
	public static class Wi extends ActionManageOpinionWi {

		private static final long serialVersionUID = -1063020713099914626L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageOpinion.Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -4433459196487510338L;
	}

}