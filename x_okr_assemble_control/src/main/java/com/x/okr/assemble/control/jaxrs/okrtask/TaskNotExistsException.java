package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class TaskNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskNotExistsException( String id ) {
		super("指定的待办信息不存在!ID:" + id );
	}
	
}
