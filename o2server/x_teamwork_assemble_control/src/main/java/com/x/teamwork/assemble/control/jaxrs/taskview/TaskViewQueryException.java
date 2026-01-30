package com.x.teamwork.assemble.control.jaxrs.taskview;

import com.x.base.core.project.exception.PromptException;

class TaskViewQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskViewQueryException( Throwable e ) {
		super("系统在查询工作任务视图信息时发生异常。" , e );
	}
	
	TaskViewQueryException( Throwable e, String message ) {
		super("系统在查询工作任务视图信息时发生异常。Message:" + message, e );
	}
}
