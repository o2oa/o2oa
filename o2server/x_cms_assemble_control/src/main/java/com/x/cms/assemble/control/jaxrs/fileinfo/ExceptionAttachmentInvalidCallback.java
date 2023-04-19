package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentInvalidCallback extends PromptException {

	private static final long serialVersionUID = 8275405268546054638L;

	ExceptionAttachmentInvalidCallback(String callbackName, String fileName) {
		super(callbackName, "附件:{}, 不符合上传类型.", fileName);
	}

	ExceptionAttachmentInvalidCallback(String callbackName, String fileName, Integer fileSize) {
		super(callbackName, "附件:{},附件大小超过限制{}M.", fileName, fileSize);
	}

}
