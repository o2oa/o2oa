package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class ExceptionModifyConfig extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionModifyConfig( ) {
		super("已禁用config修改.");
	}
}
