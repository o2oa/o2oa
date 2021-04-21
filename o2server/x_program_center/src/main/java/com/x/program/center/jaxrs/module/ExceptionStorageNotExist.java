package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.exception.PromptException;

class ExceptionStorageNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionStorageNotExist(String name) {
		super("无法找到存储器: {}.", name);
	}
}
