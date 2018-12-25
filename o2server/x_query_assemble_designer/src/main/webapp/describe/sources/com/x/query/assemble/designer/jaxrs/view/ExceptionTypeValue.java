package com.x.query.assemble.designer.jaxrs.view;

import com.x.base.core.project.exception.PromptException;
import com.x.query.core.entity.View;

class ExceptionTypeValue extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionTypeValue(String value) {
		super("类型值必须为 " + View.TYPE_CMS + ", 或者 " + View.TYPE_PROCESSPLATFORM + ", {} 值不可接受.", value);
	}
}
