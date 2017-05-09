package com.x.base.core.application.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.x.base.core.application.jaxrs.cache.CacheAction;
import com.x.base.core.application.jaxrs.echo.EchoAction;
import com.x.base.core.application.jaxrs.logger.LoggerAction;
import com.x.base.core.project.jaxrs.clockschedule.ClockScheduleAction;

public abstract class AbstractActionApplication extends Application {
	protected Set<Object> singletons = new HashSet<>();
	protected Set<Class<?>> classes = new HashSet<>();

	public AbstractActionApplication() {
		classes.add(EchoAction.class);
		classes.add(CacheAction.class);
		classes.add(LoggerAction.class);
		classes.add(ClockScheduleAction.class);
		// providers
		classes.add(WrapInMessageBodyReader.class);
		classes.add(MultiPartFeature.class);
	}

}
