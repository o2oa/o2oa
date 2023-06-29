package com.x.program.init.jaxrs.server;

import static com.x.base.core.project.config.Config.resource;

import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionExecute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				@SuppressWarnings("unchecked")
				LinkedBlockingQueue<String> queue = (LinkedBlockingQueue<String>) resource(
						Config.RESOURCE_INITSERVERSTOPSIGNAL);
				queue.put("stop");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
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