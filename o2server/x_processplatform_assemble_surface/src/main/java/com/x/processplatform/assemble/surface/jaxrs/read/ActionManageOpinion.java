package com.x.processplatform.assemble.surface.jaxrs.read;

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
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			Process process = business.process().pick(read.getProcess());
			Application application = business.application().pick(read.getApplication());
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Read.class);
			read.setOpinion(Objects.toString(wi.getOpinion(), ""));
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageOpinion$Wi")
	public static class Wi extends ActionManageOpinionWi {

		private static final long serialVersionUID = 8873792126262212118L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageOpinion$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7406223917057020953L;

	}

}