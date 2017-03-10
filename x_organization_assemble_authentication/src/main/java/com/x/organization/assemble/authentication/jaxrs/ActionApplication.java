package com.x.organization.assemble.authentication.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.authentication.jaxrs.authentication.AuthenticationAction;
import com.x.organization.assemble.authentication.jaxrs.bind.BindAction;
import com.x.organization.assemble.authentication.jaxrs.sso.SsoAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(AuthenticationAction.class);
		classes.add(SsoAction.class);
		classes.add(BindAction.class);
		return classes;
	}

}