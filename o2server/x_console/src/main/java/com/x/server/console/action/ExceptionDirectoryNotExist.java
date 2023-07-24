package com.x.server.console.action;

import java.nio.file.Path;

import com.x.base.core.project.exception.PromptException;

class ExceptionDirectoryNotExist extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDirectoryNotExist(Path path) {
		super("directory not exist: {}.", path.toString());
	}

}
