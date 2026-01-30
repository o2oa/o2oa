package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.exception.PromptException;

class TaskTagQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskTagQueryException( Throwable e ) {
		super("系统在查询工作任务标签信息时发生异常。" , e );
	}
	
	TaskTagQueryException( Throwable e, String message ) {
		super("系统在查询工作任务标签信息时发生异常。Message:" + message, e );
	}
}
