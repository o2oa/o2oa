package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFileNameEmpty() {
		super("文件名称不能为空");
	}
}
