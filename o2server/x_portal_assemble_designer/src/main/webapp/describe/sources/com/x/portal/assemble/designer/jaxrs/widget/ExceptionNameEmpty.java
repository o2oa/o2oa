package com.x.portal.assemble.designer.jaxrs.widget;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameEmpty extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNameEmpty() {
		super("名称不能为空");
	}
}
