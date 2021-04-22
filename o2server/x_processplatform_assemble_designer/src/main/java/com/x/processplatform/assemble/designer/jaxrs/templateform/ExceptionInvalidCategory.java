package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidCategory extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionInvalidCategory(String category) {
		super("类型校验不通过:{}.", category);
	}

}
