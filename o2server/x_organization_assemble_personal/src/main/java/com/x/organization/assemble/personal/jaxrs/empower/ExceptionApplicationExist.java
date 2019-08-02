package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationExist extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionApplicationExist(String identity, String application) {
		super("身份 {} 在指定应用 {} 的委托已经存在.", identity, application);
	}

}
