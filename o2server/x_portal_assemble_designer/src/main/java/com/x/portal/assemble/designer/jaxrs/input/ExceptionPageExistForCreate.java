package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionPageExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionPageExistForCreate(String id) {
		super("创建页面冲突, id:{}.");
	}
}
