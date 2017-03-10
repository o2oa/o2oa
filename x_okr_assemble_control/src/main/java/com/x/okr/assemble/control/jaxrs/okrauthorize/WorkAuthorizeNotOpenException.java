package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class WorkAuthorizeNotOpenException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkAuthorizeNotOpenException() {
		super("工作授权功能未被开启，无法执行授权相关操作！" );
	}
}
