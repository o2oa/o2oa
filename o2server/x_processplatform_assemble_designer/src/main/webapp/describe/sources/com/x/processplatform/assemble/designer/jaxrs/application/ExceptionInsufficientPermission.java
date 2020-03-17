package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInsufficientPermission extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionInsufficientPermission(String person) {
		super("person: {} has insufficient permission.", person);
	}
}
