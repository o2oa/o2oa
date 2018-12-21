package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionWorkNotContainsAttachmentCallback extends CallbackPromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkNotContainsAttachmentCallback(String callbackName, String title, String workId, String attachmentName,
			String attachmentId) {
		super(callbackName, "work title:{} id:{}, not contains attachment name:{} id:{} .", title, workId,
				attachmentName, attachmentId);
	}

}
