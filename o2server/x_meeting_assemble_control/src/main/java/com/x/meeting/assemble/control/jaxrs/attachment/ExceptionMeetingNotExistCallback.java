package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionMeetingNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionMeetingNotExistCallback(String callbackName, String id) {
		super(callbackName, "会议: {} 不存在.", id);
	}
}
