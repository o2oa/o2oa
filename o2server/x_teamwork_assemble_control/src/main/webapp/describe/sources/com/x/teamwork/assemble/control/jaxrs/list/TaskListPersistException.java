package com.x.teamwork.assemble.control.jaxrs.list;

import com.x.base.core.project.exception.PromptException;

class TaskListPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListPersistException( Throwable e ) {
		super("系统在保存工作任务列表信息时发生异常。" , e );
	}
	
	TaskListPersistException( Throwable e, String message ) {
		super("系统在保存工作任务列表信息时发生异常。Message:" + message, e );
	}
	
	TaskListPersistException( String message ) {
		super("系统在保存工作任务列表信息时发生异常。Message:" + message );
	}
}
