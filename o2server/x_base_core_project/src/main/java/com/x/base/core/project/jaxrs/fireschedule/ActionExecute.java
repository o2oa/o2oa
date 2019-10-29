package com.x.base.core.project.jaxrs.fireschedule;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

class ActionExecute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, @Context ServletContext servletContext, String className)
			throws Exception {
		logger.debug(effectivePerson, "execute:{}.", className);
		ActionResult<Wo> result = new ActionResult<>();
		com.x.base.core.project.Context ctx = com.x.base.core.project.Context.fromServletContext(servletContext);
		Class<?> clz = Class.forName(className);
		ctx.fireScheduleOnLocal((Class<AbstractJob>) clz, 1);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}