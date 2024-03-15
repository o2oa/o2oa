package com.x.portal.assemble.designer.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.portal.assemble.designer.jaxrs.designer.DesignerAction;
import com.x.portal.assemble.designer.jaxrs.dict.DictAction;
import com.x.portal.assemble.designer.jaxrs.file.FileAction;
import com.x.portal.assemble.designer.jaxrs.id.IdAction;
import com.x.portal.assemble.designer.jaxrs.input.InputAction;
import com.x.portal.assemble.designer.jaxrs.output.OutputAction;
import com.x.portal.assemble.designer.jaxrs.page.PageAction;
import com.x.portal.assemble.designer.jaxrs.pageversion.PageVersionAction;
import com.x.portal.assemble.designer.jaxrs.portal.PortalAction;
import com.x.portal.assemble.designer.jaxrs.portalcategory.PortalCategoryAction;
import com.x.portal.assemble.designer.jaxrs.script.ScriptAction;
import com.x.portal.assemble.designer.jaxrs.scriptversion.ScriptVersionAction;
import com.x.portal.assemble.designer.jaxrs.templatepage.TemplatePageAction;
import com.x.portal.assemble.designer.jaxrs.widget.WidgetAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PortalAction.class);
		classes.add(PortalCategoryAction.class);
		classes.add(WidgetAction.class);
		classes.add(FileAction.class);
		classes.add(PageAction.class);
		classes.add(ScriptAction.class);
		classes.add(TemplatePageAction.class);
		classes.add(IdAction.class);
		classes.add(OutputAction.class);
		classes.add(InputAction.class);
		classes.add(DesignerAction.class);
		classes.add(DictAction.class);
		classes.add(PageVersionAction.class);
		classes.add(ScriptVersionAction.class);
		return classes;
	}

}
