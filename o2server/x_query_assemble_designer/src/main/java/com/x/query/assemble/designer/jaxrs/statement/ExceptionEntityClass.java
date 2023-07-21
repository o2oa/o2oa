package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEntityClass extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntityClass(String className) {
		super("无法获取指定类型:{}.", className);
	}
}
