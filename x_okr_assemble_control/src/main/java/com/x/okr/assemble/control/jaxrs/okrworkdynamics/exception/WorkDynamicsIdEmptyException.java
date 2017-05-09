package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.exception.PromptException;

public class WorkDynamicsIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkDynamicsIdEmptyException() {
		super("操作动态ID为空。" );
	}
}
