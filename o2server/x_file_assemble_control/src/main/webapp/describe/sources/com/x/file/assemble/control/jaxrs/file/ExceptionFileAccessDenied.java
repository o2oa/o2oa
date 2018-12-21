package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileAccessDenied extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFileAccessDenied(String person, String name, String id) {
		super("用户: {} 访问文件: {}, id:{}, 由于权限不足被拒绝.", person, name, id);
	}
}
