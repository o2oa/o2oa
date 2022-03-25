package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAppInfoAccessDenied(String person, String name, String id) {
		super("人员:{}访问应用程序名称:{}id:{}，拒绝。", person, name, id);
	}

}
