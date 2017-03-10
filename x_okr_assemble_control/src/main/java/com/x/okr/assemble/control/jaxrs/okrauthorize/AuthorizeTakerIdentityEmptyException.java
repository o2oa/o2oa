package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class AuthorizeTakerIdentityEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeTakerIdentityEmptyException() {
		super("工作授权承担者为空，无法继续进行授权操作。");
	}
}
