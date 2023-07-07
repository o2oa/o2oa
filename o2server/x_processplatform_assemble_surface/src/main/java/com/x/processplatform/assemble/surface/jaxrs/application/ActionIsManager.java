package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionIsManager extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionIsManager.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(flag);
			if (null == application) {
				throw new ExceptionEntityNotExist(flag);
			}
			wo.setValue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, null));
		}
		result.setData(wo);
		return result;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.application.ActionIsManager$Wo")
	public static class Wo extends WrapBoolean {

	}

}
