package com.x.okr.assemble.control.jaxrs.identity.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNewIdentityEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionNewIdentityEmpty() {
		super("旧的身份名称为空，无法继续进行身份替换操作。");
	}
}
