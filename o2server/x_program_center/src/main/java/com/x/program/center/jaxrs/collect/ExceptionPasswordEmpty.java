package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -7965962660756955360L;

	ExceptionPasswordEmpty() {
		super("密码不能为空.");
	}
}
