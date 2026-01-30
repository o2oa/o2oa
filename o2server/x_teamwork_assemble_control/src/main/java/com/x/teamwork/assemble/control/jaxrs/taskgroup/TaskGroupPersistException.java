package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import com.x.base.core.project.exception.PromptException;

class TaskGroupPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskGroupPersistException( Throwable e ) {
		super("系统在保存工作任务组信息时发生异常。" , e );
	}
	
	TaskGroupPersistException( Throwable e, String message ) {
		super("系统在保存工作任务组信息时发生异常。Message:" + message, e );
	}
	
	TaskGroupPersistException( String message ) {
		super("系统在保存工作任务组信息时发生异常。Message:" + message );
	}
}
