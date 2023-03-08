package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionEntityExistForCreate extends PromptException {

	private static final long serialVersionUID = -5089329042800970731L;

	ExceptionEntityExistForCreate(String id, Class<?> cls) {
		super("新建实体冲突, id:{}, class:{}.", id, cls.getName());
	}
}
