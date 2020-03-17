package com.x.component.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.component.assemble.control.jaxrs.component.ComponentAction;
import com.x.component.assemble.control.jaxrs.status.StatusAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(ComponentAction.class);
		classes.add(StatusAction.class);
		return classes;
	}

}