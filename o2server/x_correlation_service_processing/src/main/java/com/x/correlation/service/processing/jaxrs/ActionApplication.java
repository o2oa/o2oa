package com.x.correlation.service.processing.jaxrs;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.correlation.service.processing.jaxrs.correlation.CorrelationAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public ActionApplication() {
		super();
		classes.add(CorrelationAction.class);
	}
}
