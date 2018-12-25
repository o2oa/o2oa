package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFileExistForCreate(String id) {
		super("创建文件冲突, id:{}.");
	}
}
