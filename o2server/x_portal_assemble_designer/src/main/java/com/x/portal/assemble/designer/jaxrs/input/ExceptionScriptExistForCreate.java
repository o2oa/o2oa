package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionScriptExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionScriptExistForCreate(String id) {
		super("创建脚本冲突, id:{}.");
	}
}
