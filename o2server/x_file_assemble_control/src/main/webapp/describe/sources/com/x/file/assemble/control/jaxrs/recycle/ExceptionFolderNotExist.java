package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNotExist(String id) {
		super("分享的目录: {} 不存在.", id);
	}
}
