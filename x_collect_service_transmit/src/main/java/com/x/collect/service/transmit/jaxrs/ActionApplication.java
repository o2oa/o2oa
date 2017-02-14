package com.x.collect.service.transmit.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.collect.service.transmit.jaxrs.transmit.TransmitAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(TransmitAction.class);
		return classes;
	}

}
