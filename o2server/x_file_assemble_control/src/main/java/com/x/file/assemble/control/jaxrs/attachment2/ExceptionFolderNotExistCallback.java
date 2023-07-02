package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionFolderNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNotExistCallback(String callbackName, String id) {
		super(callbackName, "指定的目录: {} 不存在.", id);
	}
}
