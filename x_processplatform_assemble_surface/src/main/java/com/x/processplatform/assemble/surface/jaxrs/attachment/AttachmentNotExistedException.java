package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class AttachmentNotExistedException extends PromptException {

	private static final long serialVersionUID = -3503683561668855227L;

	AttachmentNotExistedException(String attachmentId) {
		super("attachment id:{}, not existed.", attachmentId);
	}

}
