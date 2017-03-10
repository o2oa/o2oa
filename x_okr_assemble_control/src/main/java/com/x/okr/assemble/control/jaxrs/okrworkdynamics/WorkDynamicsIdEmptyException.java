package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import com.x.base.core.exception.PromptException;

class WorkDynamicsIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDynamicsIdEmptyException() {
		super("操作动态ID为空。" );
	}
}
