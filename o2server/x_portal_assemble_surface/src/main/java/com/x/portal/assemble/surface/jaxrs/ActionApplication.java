package com.x.portal.assemble.surface.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.portal.assemble.surface.jaxrs.dict.DictAction;
import com.x.portal.assemble.surface.jaxrs.file.FileAction;
import com.x.portal.assemble.surface.jaxrs.page.PageAction;
import com.x.portal.assemble.surface.jaxrs.portal.PortalAction;
import com.x.portal.assemble.surface.jaxrs.script.ScriptAction;
import com.x.portal.assemble.surface.jaxrs.widget.WidgetAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {
		classes.add(PortalAction.class);
		classes.add(WidgetAction.class);
		classes.add(PageAction.class);
		classes.add(ScriptAction.class);
		classes.add(FileAction.class);
		classes.add(DictAction.class);
		return classes;
	}
}
