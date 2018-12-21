package com.x.processplatform.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionApplicationAccessDenied(String person, String name, String id) {
		super("用户:{} 访问应用 name: {} id: {}, 权限不足.", person, name, id);
	}

}
