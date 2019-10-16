package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 6214553784762782200L;

	ExceptionAttachmentNotExist(String id) {
		super("回收站的文件: {} 不存在.", id);
	}
}
