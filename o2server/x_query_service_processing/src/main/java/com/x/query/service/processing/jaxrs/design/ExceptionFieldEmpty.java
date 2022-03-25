package com.x.query.service.processing.jaxrs.design;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionFieldEmpty extends LanguagePromptException {


	private static final long serialVersionUID = -87643358931771164L;

	public ExceptionFieldEmpty(String field) {
		super("参数: {} 值无效.", field);
	}

}
