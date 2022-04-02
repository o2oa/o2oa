package com.x.query.assemble.surface.jaxrs.table;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;

class ActionReloadDynamic extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReloadDynamic.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Business.reloadClassLoader();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -5755898083219447939L;

	}
}
