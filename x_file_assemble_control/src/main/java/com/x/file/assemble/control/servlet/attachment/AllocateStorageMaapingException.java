package com.x.file.assemble.control.servlet.attachment;

import com.x.base.core.exception.PromptException;

class AllocateStorageMaapingException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	AllocateStorageMaapingException() {
		super("无法分派存储器.");
	}
}
