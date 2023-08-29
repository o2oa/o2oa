package com.x.program.init.jaxrs.secret;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.MissionSetSecret;

class ActionCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheck.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson) {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(MissionSetSecret.check());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 6077991954765766298L;

	}

}