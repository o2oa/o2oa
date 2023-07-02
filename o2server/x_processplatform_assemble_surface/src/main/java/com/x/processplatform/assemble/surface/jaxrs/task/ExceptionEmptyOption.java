package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyOption extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionEmptyOption() {
		super("人员身份为空或者均已经存在办理人中.");
	}

}
