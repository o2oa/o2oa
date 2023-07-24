package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 6214553784762782200L;

	ExceptionAttachmentNotExist(String id) {
		super("分享的文件: {} 不存在.", id);
	}
	ExceptionAttachmentNotExist(String id,String fileId) {
		super("分享的文件: {} 的附件{}不存在.", id, fileId);
	}
}
