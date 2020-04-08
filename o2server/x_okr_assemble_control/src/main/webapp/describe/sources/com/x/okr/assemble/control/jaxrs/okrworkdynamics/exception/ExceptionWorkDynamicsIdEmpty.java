package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDynamicsIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDynamicsIdEmpty() {
		super("操作动态ID为空。" );
	}
}
