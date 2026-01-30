package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskTransformException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskTransformException(Throwable e ) {
		super("系统在转换工作任务为子任务时发生异常。" , e );
	}

	TaskTransformException(Throwable e, String message ) {
		super("系统在转换工作任务为子任务时发生异常。Message:" + message, e );
	}

	TaskTransformException(String message ) {
		super("系统在转换工作任务为子任务时发生异常。Message:" + message );
	}
}
