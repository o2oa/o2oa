package com.x.message.assemble.communicate.jaxrs.ws;

import javax.websocket.Session;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.WsMessage;
import com.x.message.assemble.communicate.ThisApplication;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		wo.setValue(false);
		Session session = ThisApplication.connections.get(wi.getPerson());
		if (session != null && session.isOpen()) {
			logger.debug(effectivePerson, "send ws, message: {}.", wi);
			session.getBasicRemote().sendText(jsonElement.toString());
			wo.setValue(true);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends WsMessage {
	}

	public static class Wo extends WrapBoolean {

	}

}