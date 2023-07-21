package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAllocateStorageMaaping extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAllocateStorageMaaping() {
		super("无法分派存储器.");
	}
}
