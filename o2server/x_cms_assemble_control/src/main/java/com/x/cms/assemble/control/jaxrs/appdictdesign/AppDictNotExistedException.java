package com.x.cms.assemble.control.jaxrs.appdictdesign;

import com.x.base.core.project.exception.PromptException;

class AppDictNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	AppDictNotExistedException(String str) {
		super("appInfoDict: {} not existed.", str);
	}
}
