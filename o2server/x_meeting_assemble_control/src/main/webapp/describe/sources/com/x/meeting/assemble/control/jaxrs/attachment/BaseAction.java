package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

abstract class BaseAction extends StandardJaxrsAction {
	protected Attachment concreteAttachment(Meeting meeting, Boolean summary) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setMeeting(meeting.getId());
		attachment.setSummary(summary);
		return attachment;
	}
}
