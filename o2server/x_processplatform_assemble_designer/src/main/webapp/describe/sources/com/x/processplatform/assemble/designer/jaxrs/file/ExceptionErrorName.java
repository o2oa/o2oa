package com.x.processplatform.assemble.designer.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorName extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionErrorName(String name) {
		super("{} 名称错误.");
	}
}
