package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionShareNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionShareNotExist(String id) {
		super("指定的共享文件: {} 不存在.", id);
	}
}
