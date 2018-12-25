package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoNotExistForCover extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotExistForCover(String id, String name, String alias) {
		super("无法查找到用于覆盖的应用, id:{}, name:{}, alias:{}.", id, name, alias);
	}
}
