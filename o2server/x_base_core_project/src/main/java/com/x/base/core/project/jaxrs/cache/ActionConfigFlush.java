package com.x.base.core.project.jaxrs.cache;

import javax.servlet.ServletContext;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionConfigFlush extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionConfigFlush.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext) {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Config.flush();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.base.core.project.jaxrs.cache.ActionConfigFlush$Wo")
	public static class Wo extends WrapBoolean {

	}

}