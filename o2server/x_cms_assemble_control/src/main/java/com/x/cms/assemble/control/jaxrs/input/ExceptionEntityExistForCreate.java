package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionEntityExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntityExistForCreate(String id, Class<?> cls) {
		super("新建实体冲突, id:{}, class:{}.", id, cls.getName());
	}
}
