package com.x.program.admin.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.program.admin.jaxrs.h2.H2Action;
import com.x.program.admin.jaxrs.restore.RestoreAction;
import com.x.program.admin.jaxrs.secret.SecretAction;
import com.x.program.admin.jaxrs.server.ServerAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(SecretAction.class);
		classes.add(ServerAction.class);
		classes.add(H2Action.class);
		classes.add(RestoreAction.class);
		classes.add(H2Action.class);
		return classes;
	}

}