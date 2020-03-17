package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionViewableWorkIdsQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionViewableWorkIdsQuery( Throwable e, String identity ) {
		super("系统获取用户可访问的中心工作时发生异常. Person:" + identity, e );
	}
}
