package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionStorageMappingNotExisted extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionStorageMappingNotExisted(String name) {
		super("无法找到存储器: {}.", name);
	}
}
