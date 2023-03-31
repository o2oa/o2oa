package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentInvalid extends PromptException {

	private static final long serialVersionUID = 3232548525722242208L;

	public static String defaultMessage = "附件:{}, 不符合上传类型.";

	ExceptionAttachmentInvalid(String fileName) {
		super(defaultMessage, fileName);
	}

	ExceptionAttachmentInvalid(String fileName, Integer fileSize) {
		super("附件:{},附件大小超过限制{}M.", fileName, fileSize);
	}

}
