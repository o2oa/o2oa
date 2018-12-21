package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import com.x.base.core.project.exception.PromptException;

class ApplicationDictNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ApplicationDictNotExistedException(String str) {
		super("applicationDict: {} not existed.", str);
	}
}
