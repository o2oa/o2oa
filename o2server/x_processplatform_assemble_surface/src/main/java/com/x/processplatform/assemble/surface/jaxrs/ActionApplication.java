package com.x.processplatform.assemble.surface.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.processplatform.assemble.surface.jaxrs.application.ApplicationAction;
import com.x.processplatform.assemble.surface.jaxrs.applicationdict.ApplicationDictAction;
import com.x.processplatform.assemble.surface.jaxrs.attachment.AttachmentAction;
import com.x.processplatform.assemble.surface.jaxrs.control.ControlAction;
import com.x.processplatform.assemble.surface.jaxrs.data.DataAction;
import com.x.processplatform.assemble.surface.jaxrs.file.FileAction;
import com.x.processplatform.assemble.surface.jaxrs.form.FormAction;
import com.x.processplatform.assemble.surface.jaxrs.job.JobAction;
import com.x.processplatform.assemble.surface.jaxrs.process.ProcessAction;
import com.x.processplatform.assemble.surface.jaxrs.read.ReadAction;
import com.x.processplatform.assemble.surface.jaxrs.readcompleted.ReadCompletedAction;
import com.x.processplatform.assemble.surface.jaxrs.review.ReviewAction;
import com.x.processplatform.assemble.surface.jaxrs.route.RouteAction;
import com.x.processplatform.assemble.surface.jaxrs.script.ScriptAction;
import com.x.processplatform.assemble.surface.jaxrs.serialnumber.SerialNumberAction;
import com.x.processplatform.assemble.surface.jaxrs.task.TaskAction;
import com.x.processplatform.assemble.surface.jaxrs.taskcompleted.TaskCompletedAction;
import com.x.processplatform.assemble.surface.jaxrs.test.TestAction;
import com.x.processplatform.assemble.surface.jaxrs.tool.ToolAction;
import com.x.processplatform.assemble.surface.jaxrs.util.UtilAction;
import com.x.processplatform.assemble.surface.jaxrs.work.WorkAction;
import com.x.processplatform.assemble.surface.jaxrs.workcompleted.WorkCompletedAction;
import com.x.processplatform.assemble.surface.jaxrs.worklog.WorkLogAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(TaskAction.class);
		classes.add(ApplicationAction.class);
		classes.add(ApplicationDictAction.class);
		classes.add(AttachmentAction.class);
		classes.add(DataAction.class);
		classes.add(ProcessAction.class);
		classes.add(ReadAction.class);
		classes.add(ReadCompletedAction.class);
		classes.add(ReviewAction.class);
		classes.add(ScriptAction.class);
		classes.add(SerialNumberAction.class);
		classes.add(TaskAction.class);
		classes.add(TaskCompletedAction.class);
		classes.add(WorkAction.class);
		classes.add(WorkCompletedAction.class);
		classes.add(JobAction.class);
		classes.add(ToolAction.class);
		classes.add(TestAction.class);
		classes.add(FileAction.class);
		classes.add(FormAction.class);
		classes.add(UtilAction.class);
		classes.add(WorkLogAction.class);
		classes.add(ControlAction.class);
		classes.add(RouteAction.class);
		return classes;
	}
}
