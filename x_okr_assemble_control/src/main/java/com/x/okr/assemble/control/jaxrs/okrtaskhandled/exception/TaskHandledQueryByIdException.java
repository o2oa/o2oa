package com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception;

import com.x.base.core.exception.PromptException;

public class TaskHandledQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public TaskHandledQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定的已办信息时发生异常!ID:" + id, e );
	}
}
