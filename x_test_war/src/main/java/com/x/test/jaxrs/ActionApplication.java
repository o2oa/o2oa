package com.x.test.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.x.test.jaxrs.test.TestAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public Set<Class<?>> getClasses() {
		classes.add(TestAction.class);
		// providers
		// classes.add(WrapInMessageBodyReader.class);
		return classes;
	}

	public Set<Object> getSingletons() {
		return singletons;
	}
}
