package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAllocateStorageMaapingCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAllocateStorageMaapingCallback(String callbackName) {
		super(callbackName, "无法分派存储器.");
	}
}
