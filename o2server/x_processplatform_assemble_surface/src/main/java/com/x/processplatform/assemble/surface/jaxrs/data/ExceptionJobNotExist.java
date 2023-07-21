package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJobNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionJobNotExist(String job) {
		super("指定的job不存在:{}.", job);
	}

}
