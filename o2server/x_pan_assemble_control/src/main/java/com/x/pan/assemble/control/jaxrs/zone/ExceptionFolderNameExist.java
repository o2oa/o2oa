package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNameExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNameExist(String name) {
		super("指定的共享区:{}已经存在.", name);
	}
}
