package com.x.base.core.project.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.x.base.core.project.jaxrs.cache.CacheAction;
import com.x.base.core.project.jaxrs.echo.EchoAction;
import com.x.base.core.project.jaxrs.fireschedule.FireScheduleAction;
import com.x.base.core.project.jaxrs.openapi.OpenApiAction;
import com.x.base.core.project.jaxrs.sysresource.SysResourceAction;

public abstract class AbstractActionApplication extends Application {
	protected Set<Class<?>> classes = new HashSet<>();

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	protected AbstractActionApplication() {
		classes.add(EchoAction.class);
		classes.add(CacheAction.class);
		classes.add(FireScheduleAction.class);
		classes.add(SysResourceAction.class);
		classes.add(OpenApiAction.class);
		// providers
		classes.add(MessageBodyReaderImpl.class);
		classes.add(MultiPartFeature.class);
	}

}
