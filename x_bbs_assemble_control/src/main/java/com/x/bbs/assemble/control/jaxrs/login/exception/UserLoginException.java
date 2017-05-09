package com.x.bbs.assemble.control.jaxrs.login.exception;

import com.x.base.core.exception.PromptException;

public class UserLoginException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public UserLoginException( Throwable e, String name ) {
		super("用户进行系统登入时发生异常! Person:" + name, e );
	}
}
