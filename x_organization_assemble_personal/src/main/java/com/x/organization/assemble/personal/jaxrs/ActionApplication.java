package com.x.organization.assemble.personal.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.personal.jaxrs.password.PasswordAction;
import com.x.organization.assemble.personal.jaxrs.person.PersonAction;
import com.x.organization.assemble.personal.jaxrs.regist.RegistAction;
import com.x.organization.assemble.personal.jaxrs.reset.ResetAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PersonAction.class);
		classes.add(PasswordAction.class);
		classes.add(ResetAction.class);
		classes.add(RegistAction.class);
		return classes;
	}
}
