package com.x.base.core.project.jaxrs.cache;

import javax.servlet.ServletContext;

import com.google.gson.JsonElement;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionReceive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReceive.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext, JsonElement jsonElement)
			throws Exception {
		logger.debug(effectivePerson, "receive:{}.", jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		// Object o =
		// servletContext.getAttribute(com.x.base.core.project.AbstractContext.class.getName());
		AbstractContext ctx = AbstractContext.fromServletContext(servletContext);
		if (null != ctx.clearCacheRequestQueue()) {
			ctx.clearCacheRequestQueue().send(wi);
		} else {
			ApplicationCache.receive(wi);
			CacheManager.receive(wi);
		}
		result.setData(new Wo(wi.getClassName()));
		return result;
	}

	public static class Wo extends WrapString {

		public Wo(String str) {
			super(str);
		}

	}

	public static class Wi extends WrapClearCacheRequest {

	}

}