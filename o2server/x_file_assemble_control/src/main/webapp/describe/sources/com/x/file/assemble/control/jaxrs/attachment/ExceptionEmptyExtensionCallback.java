package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionEmptyExtensionCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyExtensionCallback(String callbackName,String name) {
		super(callbackName,"不能上传文件扩展名为空的文件: {}.", name);
	}
}
