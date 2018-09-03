package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDynamicsSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDynamicsSave( Throwable e ) {
		super("系统在保存操作动态信息时发生异常.", e );
	}
}
