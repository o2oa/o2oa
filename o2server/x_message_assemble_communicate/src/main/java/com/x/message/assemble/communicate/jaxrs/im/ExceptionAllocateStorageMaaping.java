package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionAllocateStorageMaaping extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAllocateStorageMaaping() {
		super("无法分派存储器.");
	}
}
