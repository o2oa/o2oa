package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.exception.PromptException;

public class WorkDynamicsDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkDynamicsDeleteException( Throwable e, String id ) {
		super("系统在删除操作动态信息时发生异常. ID:" + id, e );
	}
}
