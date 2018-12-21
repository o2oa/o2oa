package com.x.base.core.project.jaxrs.logger;

import javax.servlet.ServletContext;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTrace extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTrace.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
				.getAttribute(com.x.base.core.project.Context.class.getName());
		logger.info("{} change logger level to TRACE.", ctx.clazz().getName());
		LoggerFactory.setLevelTrace();
		result.setData(new Wo(true));
		return result;
	}

	public static class Wo extends WrapBoolean {

		public Wo(boolean value) {
			super(value);
		}

	}

}