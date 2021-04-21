package com.x.cms.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionErrorName extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionErrorName(String name) {
		super("{} 名称错误.", name);
	}
}
