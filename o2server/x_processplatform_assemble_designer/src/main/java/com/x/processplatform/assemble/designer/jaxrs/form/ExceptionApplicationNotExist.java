package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationNotExist(String flag) {
		super("指定的应用不存在:{}.", flag);
	}
}
