package com.x.program.center.jaxrs.deploy;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeployDisable extends PromptException {

	private static final long serialVersionUID = -5285650034988505084L;

	public ExceptionDeployDisable() {
		super("禁用部署.");
	}

}
