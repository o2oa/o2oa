package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionApplicationAccessDenied(String person, String id) {
		super("用户:{} 无权限访问应用 id:{}.", person, id);
	}

}
