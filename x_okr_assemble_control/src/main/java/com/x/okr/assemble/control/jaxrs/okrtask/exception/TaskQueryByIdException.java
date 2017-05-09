package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.exception.PromptException;

public class TaskQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public TaskQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定的待办信息时发生异常!ID:" + id, e );
	}
}
