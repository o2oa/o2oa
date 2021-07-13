package com.x.query.assemble.designer.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAccessDenied(String person) {
		super("用户:{} 访问被拒绝.", person);
	}

}
