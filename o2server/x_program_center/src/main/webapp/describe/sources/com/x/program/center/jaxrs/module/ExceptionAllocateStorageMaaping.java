package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.PromptException;

class ExceptionAllocateStorageMaaping extends PromptException {

	private static final long serialVersionUID = 1548696203797537170L;

	ExceptionAllocateStorageMaaping() {
		super("无法分派存储器.");
	}
}
