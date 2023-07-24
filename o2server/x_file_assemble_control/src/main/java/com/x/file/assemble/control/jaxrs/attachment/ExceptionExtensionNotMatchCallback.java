package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionExtensionNotMatchCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionExtensionNotMatchCallback(String callbackName, String name, String extension) {
		super(callbackName, "文件: {} 的扩展名不匹配,期望的扩展名: {}.", name);
	}
}
