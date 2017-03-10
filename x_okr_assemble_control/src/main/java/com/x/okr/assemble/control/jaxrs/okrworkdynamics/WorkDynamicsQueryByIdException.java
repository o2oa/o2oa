package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import com.x.base.core.exception.PromptException;

class WorkDynamicsQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDynamicsQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的操作动态信息时发生异常。ID：" + id, e );
	}
}
