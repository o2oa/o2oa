package com.x.message.assemble.communicate.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.message.assemble.communicate.jaxrs.connector.ConnectorAction;
import com.x.message.assemble.communicate.jaxrs.consume.ConsumeAction;
import com.x.message.assemble.communicate.jaxrs.instant.InstantAction;
import com.x.message.assemble.communicate.jaxrs.mass.MassAction;
import com.x.message.assemble.communicate.jaxrs.message.MessageAction;
import com.x.message.assemble.communicate.jaxrs.ws.WsAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(ConnectorAction.class);
		classes.add(WsAction.class);
		classes.add(ConsumeAction.class);
		classes.add(MassAction.class);
		classes.add(MessageAction.class);
		classes.add(InstantAction.class);
		return classes;
	}

}
