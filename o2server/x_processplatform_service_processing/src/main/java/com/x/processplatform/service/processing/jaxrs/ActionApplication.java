package com.x.processplatform.service.processing.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.processplatform.service.processing.jaxrs.applicationdict.ApplicationDictAction;
import com.x.processplatform.service.processing.jaxrs.attachment.AttachmentAction;
import com.x.processplatform.service.processing.jaxrs.data.DataAction;
import com.x.processplatform.service.processing.jaxrs.documentversion.DocumentVersionAction;
import com.x.processplatform.service.processing.jaxrs.event.EventAction;
import com.x.processplatform.service.processing.jaxrs.form.FormAction;
import com.x.processplatform.service.processing.jaxrs.job.JobAction;
import com.x.processplatform.service.processing.jaxrs.read.ReadAction;
import com.x.processplatform.service.processing.jaxrs.readcompleted.ReadCompletedAction;
import com.x.processplatform.service.processing.jaxrs.record.RecordAction;
import com.x.processplatform.service.processing.jaxrs.review.ReviewAction;
import com.x.processplatform.service.processing.jaxrs.service.ServiceAction;
import com.x.processplatform.service.processing.jaxrs.snap.SnapAction;
import com.x.processplatform.service.processing.jaxrs.task.TaskAction;
import com.x.processplatform.service.processing.jaxrs.taskcompleted.TaskCompletedAction;
import com.x.processplatform.service.processing.jaxrs.touch.TouchAction;
import com.x.processplatform.service.processing.jaxrs.work.WorkAction;
import com.x.processplatform.service.processing.jaxrs.workcompleted.WorkCompletedAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(WorkAction.class);
		classes.add(WorkCompletedAction.class);
		classes.add(JobAction.class);
		classes.add(TaskAction.class);
		classes.add(TaskCompletedAction.class);
		classes.add(ReadAction.class);
		classes.add(ReadCompletedAction.class);
		classes.add(ReviewAction.class);
		classes.add(AttachmentAction.class);
		classes.add(DataAction.class);
		classes.add(ApplicationDictAction.class);
		classes.add(DocumentVersionAction.class);
		classes.add(TouchAction.class);
		classes.add(RecordAction.class);
		classes.add(ServiceAction.class);
		classes.add(SnapAction.class);
		classes.add(FormAction.class);
		classes.add(EventAction.class);
		return classes;
	}

}