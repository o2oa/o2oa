package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInsufficientPermission extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionInsufficientPermission(String person) {
		super("用户: {} 没有权限进行此操作.", person);
	}
}
