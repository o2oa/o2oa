package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionModifyConfig extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionModifyConfig( ) {
		super("已禁用config修改.");
	}
}
