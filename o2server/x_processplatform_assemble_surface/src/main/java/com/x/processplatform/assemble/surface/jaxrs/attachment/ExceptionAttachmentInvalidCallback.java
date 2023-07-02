package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentInvalidCallback extends CallbackPromptException {

	private static final long serialVersionUID = 8275405268546054638L;

	ExceptionAttachmentInvalidCallback(String callbackName, String fileName) {
		super(callbackName, "附件:{}, 不符合上传类型.", fileName);
	}

	ExceptionAttachmentInvalidCallback(String callbackName, String fileName, Integer fileSize) {
		super(callbackName, "附件:{},附件大小超过限制{}M.", fileName, fileSize);
	}

}
