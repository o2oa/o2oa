package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import com.x.base.core.project.exception.PromptException;

class TaskGroupQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskGroupQueryException( Throwable e ) {
		super("系统在查询工作任务组信息时发生异常。" , e );
	}
	
	TaskGroupQueryException( Throwable e, String message ) {
		super("系统在查询工作任务组信息时发生异常。Message:" + message, e );
	}
}
