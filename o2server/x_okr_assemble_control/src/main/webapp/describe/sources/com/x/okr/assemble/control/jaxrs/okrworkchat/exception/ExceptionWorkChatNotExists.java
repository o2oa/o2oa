package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkChatNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkChatNotExists( String id ) {
		super("指定的工作交流信息记录不存在。ID:" + id );
	}
}
