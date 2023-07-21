package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionMeetingNotExist extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionMeetingNotExist(String id) {
		super("会议: {} 不存在.", id);
	}
}
