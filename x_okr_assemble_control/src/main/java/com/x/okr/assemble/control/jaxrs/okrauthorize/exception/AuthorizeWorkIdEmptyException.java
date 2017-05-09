package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.exception.PromptException;

public class AuthorizeWorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AuthorizeWorkIdEmptyException() {
		super("授权工作id为空，无法继续进行授权操作。");
	}
}
