package com.x.base.core.project.jaxrs.thread;

import javax.servlet.ServletContext;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionAlive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAlive.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext, String name)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AbstractContext ctx = com.x.base.core.project.Context.fromServletContext(servletContext);
		Wo wo = new Wo();
		wo.setValue(ctx.threadFactory().aliveLocal(name));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = 6928521934755900177L;
	}

}