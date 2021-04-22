package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeProcessExist extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeProcessExist(String identity, String process) {
		super("身份 {} 在指定的流程 {} 的授权已经存在.", identity, process);
	}

}
