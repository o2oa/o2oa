package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.exception.PromptException;

public class TaskDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public TaskDeleteException( Throwable e, String id ) {
		super("系统根据ID删除指定的待办信息时发生异常!ID:" + id, e );
	}
}
