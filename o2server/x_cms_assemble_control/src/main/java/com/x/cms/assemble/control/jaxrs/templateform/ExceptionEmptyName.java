package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyName extends LanguagePromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionEmptyName() {
		super("名称不能为空.");
	}

}
