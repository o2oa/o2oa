package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionStorageNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionStorageNotExistCallback(String callbackName, String name) {
		super(callbackName, "无法找到存储器: {}.", name);
	}
}
