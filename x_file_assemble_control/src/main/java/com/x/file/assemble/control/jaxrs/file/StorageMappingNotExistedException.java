package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.exception.PromptException;

class StorageMappingNotExistedException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	StorageMappingNotExistedException(String name) {
		super("无法找到存储器: {}.", name);
	}
}
