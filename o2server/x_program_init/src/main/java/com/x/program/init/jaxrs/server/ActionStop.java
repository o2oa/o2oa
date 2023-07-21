package com.x.program.init.jaxrs.server;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionStop extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionStop.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) {

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				Config.resource_commandQueue().put("stop init");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.error(e);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}).start();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7892218945591687635L;

	}

}