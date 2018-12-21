package com.x.collaboration.assemble.websocket.jaxrs.message;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.collaboration.assemble.websocket.ThisApplication;
import com.x.collaboration.core.message.dialog.DialogMessage;
import com.x.collaboration.core.message.notification.NotificationMessage;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	boolean sendNotificationOnLocal(JsonElement jsonElement) throws Exception {
		String person = NotificationMessage.extractPerson(jsonElement);
		if (StringUtils.isNotEmpty(person)) {
			Session session = ThisApplication.connections.get(person);
			if (session != null && session.isOpen()) {
				logger.debug("send notification on local, person:{},message:{}.", person, jsonElement);
				session.getBasicRemote().sendText(jsonElement.toString());
				return true;
			}
		}
		return false;
	}

	boolean sendDialogOnLocal(JsonElement jsonElement) throws Exception {
		String person = DialogMessage.extractPerson(jsonElement);
		if (StringUtils.isNotEmpty(person)) {
			Session session = ThisApplication.connections.get(person);
			if (session != null && session.isOpen()) {
				logger.debug("send dialog on local, person:{},message:{}.", person, jsonElement);
				session.getBasicRemote().sendText(jsonElement.toString());
				return true;
			}
		}
		return false;
	}

}
