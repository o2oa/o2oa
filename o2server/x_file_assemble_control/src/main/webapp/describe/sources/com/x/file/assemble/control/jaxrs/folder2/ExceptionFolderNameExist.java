package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNameExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNameExist(String person, String name, String superior) {
		super("用户:{},指定的目录:{}已经存在,上级目录:{}.", person, name, superior);
	}
}
