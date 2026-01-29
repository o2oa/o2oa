package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentNotExistCallback(String callbackName, String id) {
		super(callbackName, "指定的文件: {} 不存在.", id);
	}
}
