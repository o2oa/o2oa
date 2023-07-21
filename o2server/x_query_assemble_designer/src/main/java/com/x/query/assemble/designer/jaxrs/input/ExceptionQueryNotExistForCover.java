package com.x.query.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryNotExistForCover extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionQueryNotExistForCover(String id, String name, String alias) {
		super("无法查找到用于覆盖的应用, id:{}, name:{}, alias:{}.", id, name, alias);
	}
}
