package com.x.portal.assemble.designer.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.portal.assemble.designer.jaxrs.id.IdAction;
import com.x.portal.assemble.designer.jaxrs.menu.MenuAction;
import com.x.portal.assemble.designer.jaxrs.page.PageAction;
import com.x.portal.assemble.designer.jaxrs.portal.PortalAction;
import com.x.portal.assemble.designer.jaxrs.portalcategory.PortalCategoryAction;
import com.x.portal.assemble.designer.jaxrs.script.ScriptAction;
import com.x.portal.assemble.designer.jaxrs.source.SourceAction;
import com.x.portal.assemble.designer.jaxrs.templatepage.TemplatePageAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PortalAction.class);
		classes.add(PortalCategoryAction.class);
		classes.add(MenuAction.class);
		classes.add(PageAction.class);
		classes.add(SourceAction.class);
		classes.add(ScriptAction.class);
		classes.add(TemplatePageAction.class);
		classes.add(IdAction.class);
		return classes;
	}

}