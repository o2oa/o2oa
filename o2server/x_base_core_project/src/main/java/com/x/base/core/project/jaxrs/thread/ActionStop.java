package com.x.base.core.project.jaxrs.thread;

import javax.servlet.ServletContext;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionStop extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStop.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, ServletContext servletContext, String name)
			throws Exception {
		ActionResult<JsonElement> result = new ActionResult<>();
		com.x.base.core.project.Context ctx = com.x.base.core.project.Context.fromServletContext(servletContext);
		result.setData(gson.toJsonTree(ctx.threadFactory().stop(name)));
		return result;
	}

}