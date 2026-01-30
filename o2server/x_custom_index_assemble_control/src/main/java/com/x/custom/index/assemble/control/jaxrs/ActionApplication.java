package com.x.custom.index.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.custom.index.assemble.control.jaxrs.custom.CustomAction;
import com.x.custom.index.assemble.control.jaxrs.index.IndexAction;
import com.x.custom.index.assemble.control.jaxrs.reveal.RevealAction;

/**
 * Jaxrs服务注册类，在此类中注册的Action会向外提供服务
 */
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {

		this.classes.add(IndexAction.class);
		this.classes.add(RevealAction.class);
		this.classes.add(CustomAction.class);

		return this.classes;
	}

}