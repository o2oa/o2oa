package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDynamicsQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDynamicsQueryById( Throwable e, String id ) {
		super("查询指定ID的操作动态信息时发生异常。ID：" + id, e );
	}
}
