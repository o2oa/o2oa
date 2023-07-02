package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionTemplateFormNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionTemplateFormNotExist(String flag) {
		super("指定的表单不存在:{}.", flag);
	}
}
