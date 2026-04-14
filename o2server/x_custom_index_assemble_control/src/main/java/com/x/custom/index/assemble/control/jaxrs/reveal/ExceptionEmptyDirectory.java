package com.x.custom.index.assemble.control.jaxrs.reveal;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyDirectory extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEmptyDirectory() {
		super("empty directory.");
	}
}
