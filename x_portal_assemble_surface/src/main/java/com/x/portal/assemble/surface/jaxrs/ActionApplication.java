package com.x.portal.assemble.surface.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.portal.assemble.surface.jaxrs.menu.MenuAction;
import com.x.portal.assemble.surface.jaxrs.page.PageAction;
import com.x.portal.assemble.surface.jaxrs.portal.PortalAction;
import com.x.portal.assemble.surface.jaxrs.script.ScriptAction;
import com.x.portal.assemble.surface.jaxrs.source.SourceAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PortalAction.class);
		classes.add(MenuAction.class);
		classes.add(PageAction.class);
		classes.add(SourceAction.class);
		classes.add(ScriptAction.class);
		return classes;
	}
}
