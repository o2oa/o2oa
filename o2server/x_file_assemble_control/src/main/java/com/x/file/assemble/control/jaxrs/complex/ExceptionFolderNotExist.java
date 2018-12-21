package com.x.file.assemble.control.jaxrs.complex;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionFolderNotExist(String name) {
		super("不能上传文件扩展名为空的文件: {}.", name);
	}
}
