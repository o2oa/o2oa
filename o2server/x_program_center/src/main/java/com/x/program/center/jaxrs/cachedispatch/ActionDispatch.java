package com.x.program.center.jaxrs.cachedispatch;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.x.base.core.project.Application;
import com.x.base.core.project.config.CenterServer;
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

	private static final Logger logger = LoggerFactory.getLogger(ActionDispatch.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		logger.debug("receive dispatch cache request: {}", XGsonBuilder.toJson(jsonElement));
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Map<String, List<String>> map = (Map<String, List<String>>) Config.resource(Config.RESOURCE_CONTAINERENTITIES);
		for (Entry<String, List<String>> entry : map.entrySet()) {
			if (entry.getValue().contains(wi.getClassName())) {
				CompletableFuture.runAsync(() -> {
					try {
						dispatch(effectivePerson, wi, entry, ThisApplication.context().applications().get(entry.getKey()));
					} catch (Exception e) {
						logger.error(e);
					}
				}, ThisApplication.forkJoinPool());
			}
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private void dispatch(EffectivePerson effectivePerson, Wi wi, Entry<String, List<String>> entry,
			List<Application> apps) throws Exception {
		if (ListTools.isNotEmpty(apps)) {
			for (Application o : apps) {
				String url = o.getUrlJaxrsRoot() + "cache";
				logger.debug("dispatch cache request to : {}", url);
				try {
					CipherConnectionAction.post(effectivePerson.getDebugger(), url, wi);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		} else {
			logger.debug("{}通知center更新自身缓存:{}", wi.getClassName(), entry.getKey());
			List<Entry<String, CenterServer>> centerList = Config.nodes().centerServers().orderedEntry();
			for (Entry<String, CenterServer> centerEntry : centerList) {
				try {
					CipherConnectionAction.post(effectivePerson.getDebugger(),
							Config.url_x_program_center_jaxrs(centerEntry, "cache"), wi);

				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}

	public static class Wi extends WrapClearCacheRequest {

		private static final long serialVersionUID = 2433450688317735973L;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7259210154112758607L;

	}

}
