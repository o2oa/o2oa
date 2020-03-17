package com.x.program.center.jaxrs.pms;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.PmsMessage;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = gson.fromJson(jsonElement, Wi.class);
		Wo wo = new Wo();
		wo.setValue(false);
		if (Config.collect().getEnable()) {
			wi.setUnit(Config.collect().getName());
			wi.setPassword(Config.collect().getPassword());
			String url = Config.collect().url() + "/o2_collect_assemble/jaxrs/collect/pushmessage/transfer";
			ConnectionAction.post(url, null, wi);
			wo.setValue(true);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

	static class Wi extends PmsMessage {

	}

}
