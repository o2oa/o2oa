package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;

class ActionBuild extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBuild.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			if (!business.controllable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			wo.setValue(business.buildAllTable());
			LOGGER.print("build table complete!");
			result.setData(wo);

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -29157098803487513L;

	}

}