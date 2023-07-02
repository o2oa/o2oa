package com.x.query.assemble.designer.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.query.core.entity.View;

class ExceptionTypeValue extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionTypeValue(String value) {
		super("类型值必须为{}, 或者 {}, {} 值不可接受.",View.TYPE_CMS ,  View.TYPE_PROCESSPLATFORM,value);
	}
}
