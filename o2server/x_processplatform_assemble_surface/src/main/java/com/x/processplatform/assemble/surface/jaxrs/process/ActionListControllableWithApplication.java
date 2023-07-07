package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListControllableWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListControllableWithApplication.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {

		LOGGER.debug("execute:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName, () -> applicationFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			wo.setValueList(business.process().listControllableProcess(effectivePerson, application));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListControllableWithApplication$Wo")
	public static class Wo extends WrapStringList {

	}
}
