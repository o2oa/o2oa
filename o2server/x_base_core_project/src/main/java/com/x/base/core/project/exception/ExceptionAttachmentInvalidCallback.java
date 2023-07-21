package com.x.base.core.project.exception;

public class ExceptionAttachmentInvalidCallback extends PromptException {

	private static final long serialVersionUID = 8275405268546054638L;

	public ExceptionAttachmentInvalidCallback(String callbackName, String fileName) {
		super(callbackName, "附件:{}, 不符合上传类型.", fileName);
	}

	public ExceptionAttachmentInvalidCallback(String callbackName, String fileName, Integer fileSize) {
		super(callbackName, "附件:{},附件大小超过限制{}M.", fileName, fileSize);
	}

}
