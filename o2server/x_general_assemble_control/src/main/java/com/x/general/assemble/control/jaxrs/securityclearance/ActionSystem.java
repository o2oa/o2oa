package com.x.general.assemble.control.jaxrs.securityclearance;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSystem extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSystem.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(Config.ternaryManagement().getSystemSecurityClearance());
		result.setData(wo);
		return result;
	}

	public class Wo extends WrapInteger {

		private static final long serialVersionUID = 282865207847040657L;

	}

}
