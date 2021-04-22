package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = -3503683561668855227L;

	ExceptionAttachmentNotExistCallback(String callbackName, String attachmentId) {
		super(callbackName, "指定的附件不存在:{}.", attachmentId);
	}

}
