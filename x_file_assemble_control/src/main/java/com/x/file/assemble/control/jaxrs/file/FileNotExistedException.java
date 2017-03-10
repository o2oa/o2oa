package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.exception.PromptException;

class FileNotExistedException extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	FileNotExistedException(String id) {
		super("指定的文件: {} 不存在.", id);
	}
}
