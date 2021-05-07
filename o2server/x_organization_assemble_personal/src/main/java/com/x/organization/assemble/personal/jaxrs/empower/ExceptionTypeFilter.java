package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeFilter extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeFilter(String identity, String process, String filter) {
		super("身份 {} 在指定的过滤条件授权错误.", identity, process, filter);
	}

}
