package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNameEmpty() {
		super("目录名不能为空.");
	}
}
