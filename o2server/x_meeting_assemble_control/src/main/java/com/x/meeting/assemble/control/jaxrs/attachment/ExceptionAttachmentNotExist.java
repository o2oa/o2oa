package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionAttachmentNotExist(String id) {
		super("附件: {} 不存在.", id);
	}
}
