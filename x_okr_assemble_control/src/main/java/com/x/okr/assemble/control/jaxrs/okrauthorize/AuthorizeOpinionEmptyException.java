package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class AuthorizeOpinionEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeOpinionEmptyException() {
		super("工作授权意见为空，无法继续进行授权操作。");
	}
}
