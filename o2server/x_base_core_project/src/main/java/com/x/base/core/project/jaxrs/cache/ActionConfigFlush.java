package com.x.base.core.project.jaxrs.cache;

import javax.servlet.ServletContext;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionConfigFlush extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionConfigFlush.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext) throws Exception {
		logger.debug(effectivePerson, "config flush.");
		ActionResult<Wo> result = new ActionResult<>();
//		com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
//				.getAttribute(com.x.base.core.project.Context.class.getName());
		Config.flush();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}