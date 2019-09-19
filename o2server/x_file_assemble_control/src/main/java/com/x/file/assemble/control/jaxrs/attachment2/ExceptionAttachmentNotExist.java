package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentNotExist(String id) {
		super("指定的文件: {} 不存在.", id);
	}
	ExceptionAttachmentNotExist(String id,String fileId) {
		super("指定的文件: {} 的附件{}不存在.", id, fileId);
	}
}
