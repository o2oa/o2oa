package com.x.collaboration.assemble.websocket.jaxrs.message;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;

public class ActionForward extends BaseAction {

	public WrapOutBoolean execute(JsonElement jsonElement) throws Exception {
		MessageCategory category = BaseMessage.extractCategory(jsonElement);
		WrapOutBoolean wrap = new WrapOutBoolean();
		wrap.setValue(false);
		if (null != category) {
			switch (category) {
			case notification:
				wrap.setValue(this.sendNotificationOnLocal(jsonElement));
				break;
			case dialog:
				wrap.setValue(this.sendDialogOnLocal(jsonElement));
				break;
			default:
				break;
			}
		}
		return wrap;
	}

}
