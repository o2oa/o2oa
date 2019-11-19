package com.x.processplatform.assemble.designer.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.processplatform.assemble.designer.jaxrs.application.ApplicationAction;
import com.x.processplatform.assemble.designer.jaxrs.applicationcategory.ApplicationCategoryAction;
import com.x.processplatform.assemble.designer.jaxrs.applicationdict.ApplicationDictAction;
import com.x.processplatform.assemble.designer.jaxrs.elementtool.ElementToolAction;
import com.x.processplatform.assemble.designer.jaxrs.file.FileAction;
import com.x.processplatform.assemble.designer.jaxrs.form.FormAction;
import com.x.processplatform.assemble.designer.jaxrs.formversion.FormVersionAction;
import com.x.processplatform.assemble.designer.jaxrs.id.IdAction;
import com.x.processplatform.assemble.designer.jaxrs.input.InputAction;
import com.x.processplatform.assemble.designer.jaxrs.mapping.MappingAction;
import com.x.processplatform.assemble.designer.jaxrs.output.OutputAction;
import com.x.processplatform.assemble.designer.jaxrs.process.ProcessAction;
import com.x.processplatform.assemble.designer.jaxrs.processversion.ProcessVersionAction;
import com.x.processplatform.assemble.designer.jaxrs.script.ScriptAction;
import com.x.processplatform.assemble.designer.jaxrs.scriptversion.ScriptVersionAction;
import com.x.processplatform.assemble.designer.jaxrs.templateform.TemplateFormAction;
import com.x.processplatform.assemble.designer.jaxrs.workcompleted.WorkCompletedAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(ApplicationAction.class);
		classes.add(ApplicationCategoryAction.class);
		classes.add(ApplicationDictAction.class);
		classes.add(ProcessAction.class);
		classes.add(ProcessVersionAction.class);
		classes.add(FileAction.class);
		classes.add(FormAction.class);
		classes.add(FormVersionAction.class);
		classes.add(TemplateFormAction.class);
		classes.add(ScriptAction.class);
		classes.add(ScriptVersionAction.class);
		classes.add(IdAction.class);
		classes.add(InputAction.class);
		classes.add(OutputAction.class);
//		classes.add(ProjectionAction.class);
		classes.add(MappingAction.class);
		classes.add(ElementToolAction.class);
		classes.add(WorkCompletedAction.class);
		return classes;
	}

}