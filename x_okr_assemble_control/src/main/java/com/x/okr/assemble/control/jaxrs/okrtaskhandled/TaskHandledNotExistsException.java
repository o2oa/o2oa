package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import com.x.base.core.exception.PromptException;

class TaskHandledNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskHandledNotExistsException( String id ) {
		super("指定的已办信息不存在!ID:" + id );
	}
	
}
