package com.x.cms.assemble.control.jaxrs.form;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	 ExceptionAppInfoNotExist(String flag) {
		super("appInfo: {} not existed.", flag);
	}
}
