package com.x.base.core.application.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.x.base.core.application.jaxrs.cache.CacheAction;
import com.x.base.core.application.jaxrs.echo.EchoAction;
import com.x.base.core.application.jaxrs.logger.LoggerAction;

public abstract class AbstractActionApplication extends Application {
	protected Set<Object> singletons = new HashSet<>();
	protected Set<Class<?>> classes = new HashSet<>();

	public AbstractActionApplication() {
		classes.add(EchoAction.class);
		classes.add(CacheAction.class);
		classes.add(LoggerAction.class);
		// providers
		classes.add(WrapInMessageBodyReader.class);
	}

}
