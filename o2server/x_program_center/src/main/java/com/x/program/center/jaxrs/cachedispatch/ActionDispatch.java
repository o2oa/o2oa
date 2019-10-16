package com.x.program.center.jaxrs.cachedispatch;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.x.base.core.project.Application;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.ThisApplication;

class ActionDispatch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDispatch.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		logger.debug("receive dispatch cache request: {}", XGsonBuilder.toJson(jsonElement));
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Map<String, List<String>> map = (Map<String, List<String>>) Config.resource(Config.RESOURCE_CONTAINERENTITIES);
		for (Entry<String, List<String>> entry : map.entrySet()) {
			if (entry.getValue().contains(wi.getClassName())) {
				List<Application> apps = ThisApplication.context().applications().get(entry.getKey());
				if (ListTools.isNotEmpty(apps)) {
					apps.stream().forEach(o -> {
						String url = o.getUrlRoot() + "cache";
						logger.debug("dispatch cache request to : {}", url);
						try {
							CipherConnectionAction.put(effectivePerson.getDebugger(), url, wi);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			}
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends WrapClearCacheRequest {
	}

	public static class Wo extends WrapBoolean {

	}

}
