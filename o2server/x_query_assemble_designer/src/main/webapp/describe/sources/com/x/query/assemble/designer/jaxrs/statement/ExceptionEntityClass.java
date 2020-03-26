package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.PromptException;

class ExceptionEntityClass extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntityClass(String className) {
		super("无法获取指定类型:{}.", className);
	}
}
