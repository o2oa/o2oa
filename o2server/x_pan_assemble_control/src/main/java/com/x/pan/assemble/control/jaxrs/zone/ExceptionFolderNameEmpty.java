package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNameEmpty() {
		super("名称不能为空.");
	}
}
