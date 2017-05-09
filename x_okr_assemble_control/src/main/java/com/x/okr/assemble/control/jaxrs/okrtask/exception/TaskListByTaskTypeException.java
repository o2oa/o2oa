package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.exception.PromptException;

public class TaskListByTaskTypeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public TaskListByTaskTypeException( Throwable e, String person ) {
		super("系统根据待办类别查询待办信息时发生异常!Person:" + person, e );
	}
}
