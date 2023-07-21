package com.x.base.core.project.jaxrs.cache;

import javax.servlet.ServletContext;

import com.google.gson.JsonElement;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionReceive extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReceive.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		CacheManager.receive(wi);
		result.setData(new Wo(wi.getClassName()));
		return result;
	}

	@Schema(name = "com.x.base.core.project.jaxrs.cache.ActionReceive$Wo")
	public static class Wo extends WrapString {

		public Wo(String str) {
			super(str);
		}

	}

	@Schema(name = "com.x.base.core.project.jaxrs.cache.ActionReceive$Wi")
	public static class Wi extends WrapClearCacheRequest {

	}

}