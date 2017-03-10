package com.x.file.assemble.control.servlet.file;

import com.x.base.core.exception.PromptException;

class NotMultiPartRequestException extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	NotMultiPartRequestException() {
		super("请求内容不包含文件.");
	}
}
