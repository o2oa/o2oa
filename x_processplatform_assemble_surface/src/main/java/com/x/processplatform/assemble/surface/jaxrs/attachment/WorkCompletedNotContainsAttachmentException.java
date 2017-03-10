package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class WorkCompletedNotContainsAttachmentException extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	WorkCompletedNotContainsAttachmentException(String title, String workCompletedId, String attachmentName,
			String attachmentId) {
		super("workCompleted title:{} id:{}, not contains attachment name:{} id:{} .", title, workCompletedId,
				attachmentName, attachmentId);
	}

}
