package com.x.bbs.assemble.control.jaxrs.login.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserLogin extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserLogin( Throwable e, String name ) {
		super("用户进行系统登入时发生异常! Person:" + name, e );
	}

	public ExceptionUserLogin( String message ) {
		super(message );
	}
}
