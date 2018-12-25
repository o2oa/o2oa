package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionAttachmentNotExistCallback(String callbackName, String id) {
		super(callbackName, "附件: {} 不存在.", id);
	}
}
