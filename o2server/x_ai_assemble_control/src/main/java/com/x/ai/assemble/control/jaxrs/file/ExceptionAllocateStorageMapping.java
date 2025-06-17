package com.x.ai.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAllocateStorageMapping extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAllocateStorageMapping() {
		super("无法分派存储器.");
	}
}
