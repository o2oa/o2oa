package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFormNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFormNotExist(String flag) {
		super("表单: {} 不存在.", flag);
	}
}
