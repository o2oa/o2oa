package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.exception.PromptException;

class ExceptionExecuteError extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionExecuteError(String name, Exception e) {
		super(e, "调用接口运行失败,名称: {}.", name);
	}
}
