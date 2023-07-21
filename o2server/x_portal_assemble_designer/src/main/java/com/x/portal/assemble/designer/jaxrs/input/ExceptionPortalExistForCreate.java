package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionPortalExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionPortalExistForCreate(String id) {
		super("创建站点冲突, id:{}.");
	}
}
