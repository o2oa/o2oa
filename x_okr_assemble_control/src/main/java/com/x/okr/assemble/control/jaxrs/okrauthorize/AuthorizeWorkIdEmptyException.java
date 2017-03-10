package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class AuthorizeWorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeWorkIdEmptyException() {
		super("授权工作id为空，无法继续进行授权操作。");
	}
}
