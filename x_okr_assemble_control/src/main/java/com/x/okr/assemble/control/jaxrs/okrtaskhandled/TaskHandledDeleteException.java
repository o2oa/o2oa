package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import com.x.base.core.exception.PromptException;

class TaskHandledDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskHandledDeleteException( Throwable e, String id ) {
		super("系统根据ID删除指定的已办信息时发生异常!ID:" + id, e );
	}
}
