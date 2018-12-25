package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionURLParameterGetCallback extends CallbackPromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionURLParameterGetCallback( String callbackName, Throwable e ) {
		super(callbackName, "系统在解析传入的URL参数时发生异常.", e);
	}
}
