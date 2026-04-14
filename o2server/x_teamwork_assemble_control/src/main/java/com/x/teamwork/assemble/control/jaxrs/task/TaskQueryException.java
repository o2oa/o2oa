package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskQueryException( Throwable e ) {
		super("系统在查询工作任务信息时发生异常。" , e );
	}
	
	TaskQueryException( Throwable e, String message ) {
		super("系统在查询工作任务信息时发生异常。Message:" + message, e );
	}
}
