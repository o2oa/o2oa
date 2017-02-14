package com.x.collaboration.assemble.websocket.jaxrs.message;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.collaboration.assemble.websocket.ThisApplication;
import com.x.collaboration.core.message.dialog.DialogMessage;
import com.x.collaboration.core.message.notification.NotificationMessage;

public class ActionBase {

	protected boolean sendNotificationOnLocal(JsonElement jsonElement) throws Exception {
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!sendNotificationOnLocal");
//		System.out.println(jsonElement);
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!sendNotificationOnLocal");
		String person = NotificationMessage.extractPerson(jsonElement);
		if (StringUtils.isNotEmpty(person)) {
			Session session = ThisApplication.connections.get(person);
			if (session != null && session.isOpen()) {
				session.getBasicRemote().sendText(jsonElement.toString());
				return true;
			}
		}
		return false;
	}

	protected boolean sendDialogOnLocal(JsonElement jsonElement) throws Exception {
		String person = DialogMessage.extractPerson(jsonElement);
		if (StringUtils.isNotEmpty(person)) {
			Session session = ThisApplication.connections.get(person);
			if (session != null && session.isOpen()) {
				session.getBasicRemote().sendText(jsonElement.toString());
				return true;
			}
		}
		return false;
	}

}
