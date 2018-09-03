package com.x.query.assemble.surface.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.query.assemble.surface.jaxrs.query.QueryAction;
import com.x.query.assemble.surface.jaxrs.reveal.RevealAction;
import com.x.query.assemble.surface.jaxrs.stat.StatAction;
import com.x.query.assemble.surface.jaxrs.view.ViewAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(QueryAction.class);
		classes.add(ViewAction.class);
		classes.add(StatAction.class);
		classes.add(RevealAction.class);
		return classes;
	}

}
