package com.x.organization.assemble.personal.jaxrs.trust;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessExist extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionProcessExist(String identity, String application, String process) {
		super("身份 {} 在指定应用 {} 的指定 流程 {} 的委托已经存在.", identity, application, process);
	}

}
