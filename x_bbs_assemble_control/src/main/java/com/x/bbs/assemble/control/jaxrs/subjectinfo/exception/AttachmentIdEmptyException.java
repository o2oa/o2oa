package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class AttachmentIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttachmentIdEmptyException() {
		super("附件ID为空， 无法进行查询." );
	}
}
