package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoAccessDenied extends LanguagePromptException {
	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoAccessDenied() {
		super("栏目信息不允许匿名访问.");
	}

}
