package com.x.program.center.jaxrs.command;

import com.x.base.core.project.exception.PromptException;

class ExceptionCommandDisable extends PromptException {

	private static final long serialVersionUID = -7889121412455371301L;

	public ExceptionCommandDisable() {
		super("禁止命令执行.");
	}

}
