package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotContainsAttachment extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkNotContainsAttachment(String title, String workId, String attachmentName, String attachmentId) {
		super("work title:{} id:{}, not contains attachment name:{} id:{} .", title, workId, attachmentName,
				attachmentId);
	}

}
