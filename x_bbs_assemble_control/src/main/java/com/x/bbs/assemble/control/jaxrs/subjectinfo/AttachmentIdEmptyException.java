package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class AttachmentIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentIdEmptyException() {
		super("附件ID为空， 无法进行查询." );
	}
}
