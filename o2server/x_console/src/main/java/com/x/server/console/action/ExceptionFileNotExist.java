package com.x.server.console.action;


import java.nio.file.Path;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileNotExist extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionFileNotExist(Path path) {
		super("file not exist: {}.", path.toString());
	}

}
