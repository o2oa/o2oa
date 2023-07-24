package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionMobileDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 ExceptionMobileDuplicate(String mobile, String fieldName) {
		super("手机号错误:{}, {}已有值重复.", mobile, fieldName);
	}
}
