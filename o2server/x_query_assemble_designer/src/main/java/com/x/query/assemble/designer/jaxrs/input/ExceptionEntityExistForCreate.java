package com.x.query.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEntityExistForCreate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntityExistForCreate(String id, Class<?> cls) {
		super("新建实体冲突, id:{}, class:{}.", id, cls.getName());
	}
}
