package com.x.teamwork.assemble.control.jaxrs.taskview;

import com.x.base.core.project.exception.PromptException;

class TaskViewPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskViewPersistException( Throwable e ) {
		super("系统在保存工作任务视图信息时发生异常。" , e );
	}
	
	TaskViewPersistException( Throwable e, String message ) {
		super("系统在保存工作任务视图信息时发生异常。Message:" + message, e );
	}
	
	TaskViewPersistException( String message ) {
		super("系统在保存工作任务视图信息时发生异常。Message:" + message );
	}
}
