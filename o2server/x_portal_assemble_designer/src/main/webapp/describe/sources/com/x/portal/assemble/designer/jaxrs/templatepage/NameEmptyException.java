package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.PromptException;

class NameEmptyException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	NameEmptyException() {
		super("名称不能为空");
	}
}
