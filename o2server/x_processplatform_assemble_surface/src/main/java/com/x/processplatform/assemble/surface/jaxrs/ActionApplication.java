package com.x.processplatform.assemble.surface.jaxrs;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.Version;
import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.processplatform.assemble.surface.jaxrs.anonymous.AnonymousAction;
import com.x.processplatform.assemble.surface.jaxrs.application.ApplicationAction;
import com.x.processplatform.assemble.surface.jaxrs.applicationdict.ApplicationDictAction;
import com.x.processplatform.assemble.surface.jaxrs.attachment.AttachmentAction;
import com.x.processplatform.assemble.surface.jaxrs.control.ControlAction;
import com.x.processplatform.assemble.surface.jaxrs.correlation.CorrelationAction;
import com.x.processplatform.assemble.surface.jaxrs.data.DataAction;
import com.x.processplatform.assemble.surface.jaxrs.datarecord.DataRecordAction;
import com.x.processplatform.assemble.surface.jaxrs.documentversion.DocumentVersionAction;
import com.x.processplatform.assemble.surface.jaxrs.draft.DraftAction;
import com.x.processplatform.assemble.surface.jaxrs.file.FileAction;
import com.x.processplatform.assemble.surface.jaxrs.form.FormAction;
import com.x.processplatform.assemble.surface.jaxrs.handover.HandoverAction;
import com.x.processplatform.assemble.surface.jaxrs.job.JobAction;
import com.x.processplatform.assemble.surface.jaxrs.keylock.KeyLockAction;
import com.x.processplatform.assemble.surface.jaxrs.process.ProcessAction;
import com.x.processplatform.assemble.surface.jaxrs.read.ReadAction;
import com.x.processplatform.assemble.surface.jaxrs.readcompleted.ReadCompletedAction;
import com.x.processplatform.assemble.surface.jaxrs.readrecord.ReadRecordAction;
import com.x.processplatform.assemble.surface.jaxrs.record.RecordAction;
import com.x.processplatform.assemble.surface.jaxrs.review.ReviewAction;
import com.x.processplatform.assemble.surface.jaxrs.route.RouteAction;
import com.x.processplatform.assemble.surface.jaxrs.script.ScriptAction;
import com.x.processplatform.assemble.surface.jaxrs.serialnumber.SerialNumberAction;
import com.x.processplatform.assemble.surface.jaxrs.service.ServiceAction;
import com.x.processplatform.assemble.surface.jaxrs.sign.SignAction;
import com.x.processplatform.assemble.surface.jaxrs.snap.SnapAction;
import com.x.processplatform.assemble.surface.jaxrs.task.TaskAction;
import com.x.processplatform.assemble.surface.jaxrs.taskcompleted.TaskCompletedAction;
import com.x.processplatform.assemble.surface.jaxrs.taskprocessmode.TaskProcessModeAction;
import com.x.processplatform.assemble.surface.jaxrs.touch.TouchAction;
import com.x.processplatform.assemble.surface.jaxrs.work.WorkAction;
import com.x.processplatform.assemble.surface.jaxrs.workcompleted.WorkCompletedAction;
import com.x.processplatform.assemble.surface.jaxrs.worklog.WorkLogAction;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {
		@Server(url = "../../x_processplatform_assemble_surface", description = "current server.") }, info = @Info(title = "流程应用前端服务", version = Version.VALUE, description = "o2server x_processplatform_assemble_surface interface", license = @License(name = "AGPL-3.0", url = "https://www.o2oa.net/license.html"), contact = @Contact(url = "https://www.o2oa.net", name = "o2oa", email = "admin@o2oa.net")))
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public ActionApplication() {
		super();
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
		classes.add(FileAction.class);
		classes.add(FormAction.class);
		classes.add(WorkLogAction.class);
		classes.add(ControlAction.class);
		classes.add(RouteAction.class);
		classes.add(KeyLockAction.class);
		classes.add(DocumentVersionAction.class);
		classes.add(RecordAction.class);
		classes.add(ReadRecordAction.class);
		classes.add(ServiceAction.class);
		classes.add(DraftAction.class);
		classes.add(SnapAction.class);
		classes.add(AnonymousAction.class);
		classes.add(SignAction.class);
		classes.add(CorrelationAction.class);
		classes.add(DataRecordAction.class);
		classes.add(TaskProcessModeAction.class);
		classes.add(HandoverAction.class);
		classes.add(TouchAction.class);
	}
}
