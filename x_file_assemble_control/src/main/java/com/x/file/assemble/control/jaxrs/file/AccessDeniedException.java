package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.exception.PromptException;

class AccessDeniedException extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	AccessDeniedException(String person) {
		super("用户: {}, 由于权限不足访问被拒绝.", person);
	}
}
