package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessNotMatch extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionProcessNotMatch() {
		super("流程不匹配.");
	}

}
