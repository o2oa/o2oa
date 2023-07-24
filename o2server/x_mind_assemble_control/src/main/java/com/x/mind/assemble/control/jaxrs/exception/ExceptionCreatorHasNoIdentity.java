package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCreatorHasNoIdentity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCreatorHasNoIdentity( String message ) {
		super("脑图信息删除时发生异常！MESSAGE：" + message );
	}
	
	public ExceptionCreatorHasNoIdentity( Throwable e, String message ) {
		super("脑图信息删除时发生异常！MESSAGE：" + message, e );
	}
}
