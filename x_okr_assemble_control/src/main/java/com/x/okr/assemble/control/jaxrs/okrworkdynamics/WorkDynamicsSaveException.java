package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import com.x.base.core.exception.PromptException;

class WorkDynamicsSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDynamicsSaveException( Throwable e ) {
		super("系统在保存操作动态信息时发生异常.", e );
	}
}
