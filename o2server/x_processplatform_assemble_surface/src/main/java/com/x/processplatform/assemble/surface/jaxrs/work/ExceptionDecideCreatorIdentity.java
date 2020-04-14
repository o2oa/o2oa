package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionDecideCreatorIdentity extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDecideCreatorIdentity() {
		super("识别身份错误.");
	}
}
