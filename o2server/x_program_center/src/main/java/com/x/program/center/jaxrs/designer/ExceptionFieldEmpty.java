package com.x.program.center.jaxrs.designer;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionFieldEmpty extends LanguagePromptException {

	public ExceptionFieldEmpty(String field) {
		super("参数: {} 值无效.", field);
	}

}
