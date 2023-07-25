package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

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
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted.ActionManageOpinionWi;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageOpinion extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageOpinion.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ReadCompleted readCompleted = emc.find(id, ReadCompleted.class);
			if (null == readCompleted) {
				throw new ExceptionEntityNotExist(id, ReadCompleted.class);
			}
			Process process = business.process().pick(readCompleted.getProcess());
			Application application = business.application().pick(readCompleted.getApplication());
			if (BooleanUtils
					.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(ReadCompleted.class);
			readCompleted.setOpinion(Objects.toString(wi.getOpinion(), ""));
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionManageOpinion$Wi")
	public static class Wi extends ActionManageOpinionWi {

		private static final long serialVersionUID = -8939639856439314421L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionManageOpinion$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 8605395546890060617L;

	}

}