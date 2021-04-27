package com.x.processplatform.assemble.designer.jaxrs.file;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyName extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEmptyName() {
		super("名称不能为空.");
	}
}
