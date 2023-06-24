package com.x.correlation.assemble.surface.jaxrs;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.correlation.assemble.surface.jaxrs.correlation.CorrelationAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public ActionApplication() {
		super();
		classes.add(CorrelationAction.class);
	}
}
