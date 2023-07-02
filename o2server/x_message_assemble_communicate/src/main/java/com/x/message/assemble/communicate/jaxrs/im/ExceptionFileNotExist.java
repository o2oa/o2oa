package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFileNotExist(String id) {
		super("指定的文件: {} 不存在.", id);
	}
	ExceptionFileNotExist(String id, String fileId) {
		super("指定的文件: {} 的附件{}不存在.", id, fileId);
	}
}
