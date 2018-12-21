package com.x.cms.assemble.control.jaxrs.form.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAppInfoNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	public  ExceptionAppInfoNotExist(String flag) {
		super("appInfo: {} not existed.", flag);
	}
}
