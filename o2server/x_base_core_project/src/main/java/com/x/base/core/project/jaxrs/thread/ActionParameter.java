package com.x.base.core.project.jaxrs.thread;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionParameter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionParameter.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext, String name)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AbstractContext ctx = com.x.base.core.project.Context.fromServletContext(servletContext);
		Wo wo = null;
		Map<Object, Object> value = ctx.threadFactory().parameterLocal(name);
		if (null != value) {
			wo = XGsonBuilder.convert(value, Wo.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends TreeMap<Object, Object> {

		private static final long serialVersionUID = 6928521934755900177L;
	}
}