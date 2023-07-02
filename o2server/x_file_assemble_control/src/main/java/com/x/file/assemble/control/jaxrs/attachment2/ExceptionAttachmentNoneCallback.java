package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentNoneCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAttachmentNoneCallback(String callbackName, String name) {
		super(callbackName,"未上传附件: {}.", name);
	}
}
