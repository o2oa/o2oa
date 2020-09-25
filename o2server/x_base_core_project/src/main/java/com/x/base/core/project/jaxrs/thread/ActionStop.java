package com.x.base.core.project.jaxrs.thread;

import javax.servlet.ServletContext;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionStop extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStop.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext, String name)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AbstractContext ctx = com.x.base.core.project.Context.fromServletContext(servletContext);
		Wo wo = new Wo();
		wo.setValue(ctx.threadFactory().stopLocal(name));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 3426973660260791768L;
	}

}