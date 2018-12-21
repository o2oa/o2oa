package com.x.cms.assemble.control.jaxrs.form.exception;

import com.x.base.core.project.exception.PromptException;

public  class ExceptionFormNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	public  ExceptionFormNotExist(String flag) {
		super("表单: {} 不存在.", flag);
	}
}
