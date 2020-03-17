package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionShareNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionShareNameEmpty() {
		super("分享的文件不能为空.");
	}
}
