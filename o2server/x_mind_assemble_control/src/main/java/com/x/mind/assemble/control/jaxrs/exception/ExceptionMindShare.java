package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindShare extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindShare( String message ) {
		super("脑图信息分享时发生异常！MESSAGE：" + message );
	}
	
	public ExceptionMindShare( Throwable e, String message ) {
		super("脑图信息分享时发生异常！MESSAGE：" + message, e );
	}
}
