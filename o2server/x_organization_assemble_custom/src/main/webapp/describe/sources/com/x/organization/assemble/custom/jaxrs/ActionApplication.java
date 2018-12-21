package com.x.organization.assemble.custom.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.custom.jaxrs.custom.CustomAction;
import com.x.organization.assemble.custom.jaxrs.definition.DefinitionAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(CustomAction.class);
		classes.add(DefinitionAction.class);
		return classes;
	}

}
