package com.x.portal.assemble.designer.jaxrs.dict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = 1669772685559825625L;

	ExceptionApplicationAccessDenied(String person, String name, String id) {
		super("用户:{} 没有权限访问应用 name: {} id: {}.", person, name, id);
	}

}
