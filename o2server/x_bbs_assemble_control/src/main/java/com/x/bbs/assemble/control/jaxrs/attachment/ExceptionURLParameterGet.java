package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLParameterGet extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionURLParameterGet( Throwable e ) {
		super("系统在解析传入的URL参数时发生异常.", e);
	}
}
