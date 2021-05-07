package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotExistWithIdentity extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionPersonNotExistWithIdentity(String identity) {
		super("无法定位 {} 的人员.", identity);
	}

}
