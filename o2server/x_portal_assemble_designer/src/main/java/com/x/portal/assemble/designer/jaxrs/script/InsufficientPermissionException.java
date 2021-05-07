package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class InsufficientPermissionException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	InsufficientPermissionException(String person) {
		super("用户: {} 没有权限进行此操作.", person);
	}
}
