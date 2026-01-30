package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.exception.PromptException;

class TaskTagPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskTagPersistException( Throwable e ) {
		super("系统在保存工作任务标签信息时发生异常。" , e );
	}
	
	TaskTagPersistException( Throwable e, String message ) {
		super("系统在保存工作任务标签信息时发生异常。Message:" + message, e );
	}
	
	TaskTagPersistException( String message ) {
		super("系统在保存工作任务标签信息时发生异常。Message:" + message );
	}
}
