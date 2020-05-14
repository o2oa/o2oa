package com.x.teamwork.assemble.control.jaxrs.global;

import com.x.base.core.project.exception.PromptException;

class PriorityQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PriorityQueryException( Throwable e ) {
		super("系统在查询优先级信息时发生异常。" , e );
	}
	
	PriorityQueryException( Throwable e, String message ) {
		super("系统在查询优先级信息时发生异常。Message:" + message, e );
	}
}
