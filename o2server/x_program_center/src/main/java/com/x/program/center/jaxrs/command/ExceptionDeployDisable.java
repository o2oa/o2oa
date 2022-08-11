package com.x.program.center.jaxrs.command;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDeployDisable extends LanguagePromptException {

	private static final long serialVersionUID = -5285650034988505084L;

	public ExceptionDeployDisable() {
		super("禁用war包部署.");
	}

}
