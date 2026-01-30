package com.x.teamwork.assemble.control.jaxrs.list;

import com.x.base.core.project.exception.PromptException;

class TaskListQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListQueryException( String message ) {
		super(message );
	}
	
	TaskListQueryException( Throwable e ) {
		super("系统在查询工作任务列表信息时发生异常。" , e );
	}
	
	TaskListQueryException( Throwable e, String message ) {
		super("系统在查询工作任务列表信息时发生异常。Message:" + message, e );
	}
}
