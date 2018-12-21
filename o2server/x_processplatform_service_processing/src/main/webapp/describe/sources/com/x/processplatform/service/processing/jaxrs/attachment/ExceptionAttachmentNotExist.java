package com.x.processplatform.service.processing.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = -3503683561668855227L;

	ExceptionAttachmentNotExist(String attachmentId) {
		super("attachment id:{}, not existed.", attachmentId);
	}

}
