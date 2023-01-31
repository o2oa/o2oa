package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.assemble.control.jaxrs.uuid.UUIDAction;
import com.x.base.core.project.jaxrs.AbstractActionApplication;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(UUIDAction.class);

		return this.classes;
	}

}