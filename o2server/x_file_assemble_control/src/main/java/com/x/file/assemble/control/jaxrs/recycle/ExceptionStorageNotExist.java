package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionStorageNotExist extends PromptException {

	private static final long serialVersionUID = 3604314588130414509L;

	ExceptionStorageNotExist(String name) {
		super("无法找到存储器: {}.", name);
	}
}
