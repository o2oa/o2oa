package com.x.processplatform.service.service.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.processplatform.service.service.jaxrs.service.ServiceAction;
import com.x.processplatform.service.service.jaxrs.work.WorkAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(WorkAction.class);
		classes.add(ServiceAction.class);
		classes.add(WorkAction.class);
		return classes;
	}
}