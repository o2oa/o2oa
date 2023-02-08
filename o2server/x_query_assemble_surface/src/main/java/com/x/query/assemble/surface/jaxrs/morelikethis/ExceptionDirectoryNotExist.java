package com.x.query.assemble.surface.jaxrs.morelikethis;

import com.x.base.core.project.exception.PromptException;

class ExceptionDirectoryNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDirectoryNotExist() {
		super("directory not exist.");
	}
}
