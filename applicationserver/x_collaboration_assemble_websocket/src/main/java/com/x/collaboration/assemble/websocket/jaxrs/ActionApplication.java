package com.x.collaboration.assemble.websocket.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.collaboration.assemble.websocket.jaxrs.dialog.DialogAction;
import com.x.collaboration.assemble.websocket.jaxrs.message.MessageAction;
import com.x.collaboration.assemble.websocket.jaxrs.online.OnlineAction;
import com.x.collaboration.assemble.websocket.jaxrs.sms.SMSAction;
import com.x.collaboration.assemble.websocket.jaxrs.talk.TalkAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
        classes.add(SMSAction.class);
		classes.add(OnlineAction.class);
		classes.add(MessageAction.class);
		classes.add(DialogAction.class);
		classes.add(TalkAction.class);
		return classes;
	}

}
