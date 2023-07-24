package com.x.program.center.jaxrs.zhengwudingding;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

class ActionSyncOrganizationCallback extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSyncOrganizationCallback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.info("政务钉钉接收到同步消息:{}.", jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		if (Config.zhengwuDingding().getEnable()) {
			ThisApplication.zhengwuDingdingSyncOrganizationCallbackRequest.add(jsonElement);
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
