package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionNameEmpty() {
		super("名称不能为空.");
	}
}
