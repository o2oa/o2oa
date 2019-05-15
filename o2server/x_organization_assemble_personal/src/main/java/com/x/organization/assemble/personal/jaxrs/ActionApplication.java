package com.x.organization.assemble.personal.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.personal.jaxrs.custom.CustomAction;
import com.x.organization.assemble.personal.jaxrs.definition.DefinitionAction;
import com.x.organization.assemble.personal.jaxrs.icon.IconAction;
import com.x.organization.assemble.personal.jaxrs.password.PasswordAction;
import com.x.organization.assemble.personal.jaxrs.person.PersonAction;
import com.x.organization.assemble.personal.jaxrs.regist.RegistAction;
import com.x.organization.assemble.personal.jaxrs.reset.ResetAction;
import com.x.organization.assemble.personal.jaxrs.trust.TrustAction;
import com.x.organization.assemble.personal.jaxrs.trustlog.TrustLogAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PersonAction.class);
		classes.add(PasswordAction.class);
		classes.add(ResetAction.class);
		classes.add(RegistAction.class);
		classes.add(IconAction.class);
		classes.add(DefinitionAction.class);
		classes.add(CustomAction.class);
		classes.add(TrustAction.class);
		classes.add(TrustLogAction.class);
		return classes;
	}
}
