package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import com.x.base.core.exception.PromptException;

class WorkDynamicsFilterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDynamicsFilterException( Throwable e ) {
		super("系统根据条件查询操作动态信息列表时发生异常." , e );
	}
}
