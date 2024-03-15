package com.x.program.center.jaxrs.validation;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTimeout extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTimeout.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer timeout) {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 1636014466988591350L;

	}

}
