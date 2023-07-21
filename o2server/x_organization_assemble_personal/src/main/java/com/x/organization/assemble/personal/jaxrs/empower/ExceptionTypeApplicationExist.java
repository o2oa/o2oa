package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeApplicationExist extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeApplicationExist(String identity, String application) {
		super("身份 {} 在指定应用 {} 的授权已经存在.", identity, application);
	}

}
