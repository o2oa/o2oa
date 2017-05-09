package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.exception.PromptException;

public class WorkChatNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkChatNotExistsException( String id ) {
		super("指定的工作交流信息记录不存在。ID:" + id );
	}
}
