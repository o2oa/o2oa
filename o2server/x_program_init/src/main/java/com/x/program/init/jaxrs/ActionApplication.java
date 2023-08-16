package com.x.program.init.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.program.init.jaxrs.externaldatasources.ExternalDataSourcesAction;
import com.x.program.init.jaxrs.h2.H2Action;
import com.x.program.init.jaxrs.restore.RestoreAction;
import com.x.program.init.jaxrs.secret.SecretAction;
import com.x.program.init.jaxrs.server.ServerAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(SecretAction.class);
		classes.add(ServerAction.class);
		classes.add(RestoreAction.class);
		classes.add(ExternalDataSourcesAction.class);
		classes.add(H2Action.class);
		return classes;
	}

}