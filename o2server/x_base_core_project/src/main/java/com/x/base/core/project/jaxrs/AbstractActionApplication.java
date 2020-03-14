package com.x.base.core.project.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.x.base.core.project.jaxrs.sysresource.SysResourceAction;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.x.base.core.project.jaxrs.cache.CacheAction;
import com.x.base.core.project.jaxrs.echo.EchoAction;
import com.x.base.core.project.jaxrs.fireschedule.FireScheduleAction;
import com.x.base.core.project.jaxrs.logger.LoggerAction;

public abstract class AbstractActionApplication extends Application {
	protected Set<Object> singletons = new HashSet<>();
	protected Set<Class<?>> classes = new HashSet<>();

	public AbstractActionApplication() {
		classes.add(EchoAction.class);
		classes.add(CacheAction.class);
		classes.add(LoggerAction.class);
		classes.add(FireScheduleAction.class);
		classes.add(SysResourceAction.class);
		// providers
		classes.add(MessageBodyReaderImpl.class);
		classes.add(MultiPartFeature.class);
	}

}
