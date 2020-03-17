package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskPersistException( Throwable e ) {
		super("系统在保存工作任务信息时发生异常。" , e );
	}
	
	TaskPersistException( Throwable e, String message ) {
		super("系统在保存工作任务信息时发生异常。Message:" + message, e );
	}
	
	TaskPersistException( String message ) {
		super("系统在保存工作任务信息时发生异常。Message:" + message );
	}
}
