package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExisted extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentNotExisted(String id) {
		super("指定的Attachmenet: {} 不存在.", id);
	}
}
