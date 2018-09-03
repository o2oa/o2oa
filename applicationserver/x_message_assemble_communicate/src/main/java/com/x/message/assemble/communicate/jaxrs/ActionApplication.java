package com.x.message.assemble.communicate.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.message.assemble.communicate.jaxrs.connector.ConnectorAction;
import com.x.message.assemble.communicate.jaxrs.consume.ConsumeAction;
import com.x.message.assemble.communicate.jaxrs.im.ImAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(ConnectorAction.class);
		classes.add(ImAction.class);
		classes.add(ConsumeAction.class);
		return classes;
	}

}
