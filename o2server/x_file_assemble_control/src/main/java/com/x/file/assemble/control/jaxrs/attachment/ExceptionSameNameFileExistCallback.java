package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionSameNameFileExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionSameNameFileExistCallback(String callbackName, String fileName) {
		super(callbackName, "同名文件已经存在:{}.", fileName);
	}
}