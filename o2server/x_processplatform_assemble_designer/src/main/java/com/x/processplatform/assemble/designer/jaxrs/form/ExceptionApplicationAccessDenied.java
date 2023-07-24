package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionApplicationAccessDenied(String person, String name, String id) {
		super("用户:{} 没有权限访问应用 name: {} id: {}.", person, name, id);
	}

}
